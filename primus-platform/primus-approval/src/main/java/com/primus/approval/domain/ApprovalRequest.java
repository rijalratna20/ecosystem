package com.primus.approval.domain;

import java.time.Instant;
import java.util.List;

/**
 * Represents a request to approve a sensitive data export or retrieval.
 */
public final class ApprovalRequest {

    private final String id;
    private final String transactionId;
    private final String applicationId;
    private final String requestedBy;
    private final String reason;
    private final Instant requestedAt;
    private final Instant slaDeadline;
    private ApprovalStatus status;
    private String reviewedBy;
    private Instant reviewedAt;
    private String reviewNote;
    private final List<String> sensitiveFields;

    public ApprovalRequest(String id, String transactionId, String applicationId,
                           String requestedBy, String reason, Instant requestedAt,
                           Instant slaDeadline, List<String> sensitiveFields) {
        this.id = id;
        this.transactionId = transactionId;
        this.applicationId = applicationId;
        this.requestedBy = requestedBy;
        this.reason = reason;
        this.requestedAt = requestedAt;
        this.slaDeadline = slaDeadline;
        this.status = ApprovalStatus.PENDING;
        this.sensitiveFields = sensitiveFields == null ? List.of() : List.copyOf(sensitiveFields);
    }

    public String getId() { return id; }
    public String getTransactionId() { return transactionId; }
    public String getApplicationId() { return applicationId; }
    public String getRequestedBy() { return requestedBy; }
    public String getReason() { return reason; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getSlaDeadline() { return slaDeadline; }
    public ApprovalStatus getStatus() { return status; }
    public String getReviewedBy() { return reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getReviewNote() { return reviewNote; }
    public List<String> getSensitiveFields() { return sensitiveFields; }

    /** Transition this request to APPROVED. */
    public void approve(String reviewedBy, String note) {
        requirePending();
        this.status = ApprovalStatus.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = Instant.now();
        this.reviewNote = note;
    }

    /** Transition this request to REJECTED. */
    public void reject(String reviewedBy, String note) {
        requirePending();
        this.status = ApprovalStatus.REJECTED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = Instant.now();
        this.reviewNote = note;
    }

    /** Expire this request when the SLA deadline has passed. */
    public void expire() {
        if (status == ApprovalStatus.PENDING) {
            status = ApprovalStatus.EXPIRED;
        }
    }

    private void requirePending() {
        if (status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Request " + id + " is not in PENDING state: " + status);
        }
    }

    @Override
    public String toString() {
        return "ApprovalRequest{id='" + id + "', tx='" + transactionId + "', status=" + status + '}';
    }
}
