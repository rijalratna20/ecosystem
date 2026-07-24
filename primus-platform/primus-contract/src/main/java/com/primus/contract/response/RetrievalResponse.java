package com.primus.contract.response;

import java.time.Instant;

/**
 * Response payload returned when an export is retrieved.
 */
public class RetrievalResponse {

    private String exportId;
    private String appId;
    private String transactionId;
    private String format;           // JSON or XML
    private byte[] content;
    private String checksum;
    private long sizeBytes;
    private Instant exportedAt;
    private String correlationId;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public Instant getExportedAt() { return exportedAt; }
    public void setExportedAt(Instant exportedAt) { this.exportedAt = exportedAt; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
