package com.primus.server.approval;

import com.primus.common.exception.AuthorizationException;
import com.primus.common.exception.NotFoundException;
import com.primus.common.exception.ValidationException;
import com.primus.server.audit.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Approval workflow service (Tier 2). Manages the lifecycle of approval requests
 * for sensitive export and retrieval operations.
 *
 * <p>Policy evaluation stub: sensitive operations require approval unless
 * the requestor already holds the APPROVER or ADMIN role. Extend in Tier 2
 * with a proper policy rule engine.
 */
@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    private static final long DEFAULT_SLA_SECONDS = 24 * 3600; // 24 hours

    private final Map<String, ApprovalRequest> store = new ConcurrentHashMap<>();
    private final AuditService auditService;

    public ApprovalService(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Create a new approval request and return it. The caller must poll or wait for
     * a decision before proceeding with the operation.
     */
    public ApprovalRequest createRequest(String operationType, String targetId,
                                         String requestedBy, String appId) {
        String approvalId = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(DEFAULT_SLA_SECONDS);
        ApprovalRequest request = new ApprovalRequest(
                approvalId, operationType, targetId, requestedBy, appId, expiresAt);
        store.put(approvalId, request);

        auditService.recordEvent("APPROVAL_REQUESTED", requestedBy, appId, targetId, approvalId);
        log.info("Approval requested approvalId={} type={} target={} by={}",
                approvalId, operationType, targetId, requestedBy);
        return request;
    }

    /**
     * Record an approval decision (APPROVED or REJECTED).
     */
    public ApprovalRequest decide(String approvalId, String decidedBy, boolean approved, String reason) {
        ApprovalRequest req = requireRequest(approvalId);

        if (req.getStatus() != ApprovalStatus.PENDING) {
            throw new ValidationException("Approval " + approvalId + " is already in status: " + req.getStatus());
        }
        if (req.isExpired()) {
            req.setStatus(ApprovalStatus.EXPIRED);
            throw new ValidationException("Approval request has expired", "APPROVAL_EXPIRED");
        }

        req.setStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        req.setDecidedBy(decidedBy);
        req.setDecidedAt(Instant.now());
        req.setReason(reason);

        String action = approved ? "APPROVAL_APPROVED" : "APPROVAL_REJECTED";
        auditService.recordEvent(action, decidedBy, req.getAppId(), req.getTargetId(), approvalId);
        log.info("Approval decided approvalId={} approved={} by={}", approvalId, approved, decidedBy);
        return req;
    }

    /**
     * Assert that a valid (APPROVED, non-expired) approval exists for the given operation.
     * Throws {@link AuthorizationException} with APPROVAL_REQUIRED if not.
     */
    public void requireApproval(String operationType, String targetId) {
        boolean approved = store.values().stream()
                .anyMatch(r -> r.getOperationType().equals(operationType)
                        && r.getTargetId().equals(targetId)
                        && r.getStatus() == ApprovalStatus.APPROVED
                        && !r.isExpired());
        if (!approved) {
            throw new AuthorizationException(
                    "Approval required for " + operationType + " on " + targetId, "APPROVAL_REQUIRED");
        }
    }

    public ApprovalRequest getRequest(String approvalId) {
        return requireRequest(approvalId);
    }

    public List<ApprovalRequest> getPendingForApp(String appId) {
        return store.values().stream()
                .filter(r -> appId.equals(r.getAppId()) && r.getStatus() == ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    /** Expire all pending approvals that have passed their SLA deadline. */
    public int expireStale() {
        int count = 0;
        for (ApprovalRequest req : store.values()) {
            if (req.getStatus() == ApprovalStatus.PENDING && req.isExpired()) {
                req.setStatus(ApprovalStatus.EXPIRED);
                auditService.recordEvent("APPROVAL_EXPIRED", "system",
                        req.getAppId(), req.getTargetId(), req.getApprovalId());
                count++;
            }
        }
        if (count > 0) log.info("Expired {} stale approval requests", count);
        return count;
    }

    private ApprovalRequest requireRequest(String approvalId) {
        ApprovalRequest req = store.get(approvalId);
        if (req == null) throw new NotFoundException("ApprovalRequest", approvalId);
        return req;
    }
}
