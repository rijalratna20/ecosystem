package com.primus.server.export;

import java.time.Instant;

/**
 * Immutable snapshot of an export record used by other packages.
 */
public class ExportRecordView {

    private final String exportId;
    private final String appId;
    private final String transactionId;
    private final String status;
    private final String checksum;
    private final String format;
    private final Instant completedAt;
    private final String correlationId;

    ExportRecordView(ExportRecord r) {
        this.exportId = r.getExportId();
        this.appId = r.getAppId();
        this.transactionId = r.getTransactionId();
        this.status = r.getStatus();
        this.checksum = r.getChecksum();
        this.format = r.getFormat();
        this.completedAt = r.getCompletedAt();
        this.correlationId = r.getCorrelationId();
    }

    public String getExportId() { return exportId; }
    public String getAppId() { return appId; }
    public String getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
    public String getChecksum() { return checksum; }
    public String getFormat() { return format; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCorrelationId() { return correlationId; }
}
