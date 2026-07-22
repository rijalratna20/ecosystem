package com.primus.contract.response;

import java.time.Instant;

/**
 * Describes the current state of an export operation.
 */
public class ExportStatusResponse {

    private String exportId;
    private String appId;
    private String transactionId;
    private String status;          // REQUESTED | RUNNING | COMPLETED | FAILED | CANCELLED
    private String requestedBy;
    private Instant requestedAt;
    private Instant completedAt;
    private String failureReason;
    private String correlationId;
    private int retryCount;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
