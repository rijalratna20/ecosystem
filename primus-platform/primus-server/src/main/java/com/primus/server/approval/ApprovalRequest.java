package com.primus.server.approval;

import java.time.Instant;

/**
 * An approval request created before a sensitive export/retrieval can proceed.
 */
public class ApprovalRequest {

    private final String approvalId;
    private final String operationType;   // EXPORT | RETRIEVAL
    private final String targetId;
    private final String requestedBy;
    private final String appId;
    private final Instant requestedAt;
    private final Instant expiresAt;

    private ApprovalStatus status = ApprovalStatus.PENDING;
    private String decidedBy;
    private Instant decidedAt;
    private String reason;

    public ApprovalRequest(String approvalId, String operationType, String targetId,
                           String requestedBy, String appId, Instant expiresAt) {
        this.approvalId = approvalId;
        this.operationType = operationType;
        this.targetId = targetId;
        this.requestedBy = requestedBy;
        this.appId = appId;
        this.requestedAt = Instant.now();
        this.expiresAt = expiresAt;
    }

    public String getApprovalId() { return approvalId; }
    public String getOperationType() { return operationType; }
    public String getTargetId() { return targetId; }
    public String getRequestedBy() { return requestedBy; }
    public String getAppId() { return appId; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getExpiresAt() { return expiresAt; }

    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }

    public String getDecidedBy() { return decidedBy; }
    public void setDecidedBy(String decidedBy) { this.decidedBy = decidedBy; }

    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
