package com.primus.approval.service;

import com.primus.approval.domain.ApprovalRequest;
import com.primus.approval.domain.ApprovalStatus;

import java.util.List;

/**
 * Manages the lifecycle of approval requests for sensitive data operations.
 *
 * <p>REST mapping:
 * <pre>
 *   POST /api/v1/approvals                  – create approval request
 *   GET  /api/v1/approvals/pending          – list pending requests
 *   POST /api/v1/approvals/{id}/approve     – approve a request
 *   POST /api/v1/approvals/{id}/reject      – reject with reason
 *   GET  /api/v1/approvals/{id}/history     – approval history
 * </pre>
 */
public interface ApprovalService {

    /**
     * Create a new approval request for a sensitive export transaction.
     *
     * @param request the populated (PENDING) request to persist
     * @return persisted request with generated id
     */
    ApprovalRequest createRequest(ApprovalRequest request);

    /**
     * Approve a pending request.
     *
     * @param requestId  the request identifier
     * @param reviewedBy the reviewer's principal id
     * @param note       optional approval note
     * @return updated request
     */
    ApprovalRequest approve(String requestId, String reviewedBy, String note);

    /**
     * Reject a pending request.
     *
     * @param requestId  the request identifier
     * @param reviewedBy the reviewer's principal id
     * @param note       mandatory rejection reason
     * @return updated request
     */
    ApprovalRequest reject(String requestId, String reviewedBy, String note);

    /**
     * List all requests in a given status.
     *
     * @param status the status to filter on
     * @return matching requests
     */
    List<ApprovalRequest> listByStatus(ApprovalStatus status);

    /**
     * Find a single request by id.
     *
     * @param requestId the request identifier
     * @return the request, or null if not found
     */
    ApprovalRequest findById(String requestId);

    /**
     * Expire all PENDING requests whose SLA deadline has passed.
     * Intended to be called by a scheduled job.
     *
     * @return number of requests expired
     */
    int expireOverdueRequests();
}
