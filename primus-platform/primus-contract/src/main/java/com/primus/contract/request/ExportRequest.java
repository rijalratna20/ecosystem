package com.primus.contract.request;

import java.util.Map;

/**
 * Request to initiate a new export operation.
 */
public class ExportRequest {

    private String appId;
    private String transactionId;
    private String requestedBy;
    private String correlationId;
    private String outputFormat;       // JSON or XML
    private Map<String, String> parameters;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    public Map<String, String> getParameters() { return parameters; }
    public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
}
