package com.primus.server.export;

import java.time.Instant;

/**
 * In-memory record for an export operation. Will be replaced by a JPA entity in Tier 2+.
 */
class ExportRecord {

    private final String exportId;
    private final String appId;
    private final String transactionId;
    private final String requestedBy;
    private final String correlationId;
    private final Instant requestedAt = Instant.now();

    private String status;
    private Instant completedAt;
    private String failureReason;
    private String checksum;
    private String format;
    private int retryCount;

    ExportRecord(String exportId, String appId, String transactionId,
                 String requestedBy, String correlationId) {
        this.exportId = exportId;
        this.appId = appId;
        this.transactionId = transactionId;
        this.requestedBy = requestedBy;
        this.correlationId = correlationId;
        this.status = "REQUESTED";
    }

    String getExportId() { return exportId; }
    String getAppId() { return appId; }
    String getTransactionId() { return transactionId; }
    String getRequestedBy() { return requestedBy; }
    String getCorrelationId() { return correlationId; }
    Instant getRequestedAt() { return requestedAt; }

    String getStatus() { return status; }
    void setStatus(String status) { this.status = status; }

    Instant getCompletedAt() { return completedAt; }
    void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    String getFailureReason() { return failureReason; }
    void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    String getChecksum() { return checksum; }
    void setChecksum(String checksum) { this.checksum = checksum; }

    String getFormat() { return format; }
    void setFormat(String format) { this.format = format; }

    int getRetryCount() { return retryCount; }
    void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
