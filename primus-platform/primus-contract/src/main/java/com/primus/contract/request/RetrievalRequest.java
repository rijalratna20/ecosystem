package com.primus.contract.request;

/**
 * Request to retrieve a previously completed export.
 */
public class RetrievalRequest {

    private String exportId;
    private String appId;
    private String requestedBy;
    private String correlationId;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
