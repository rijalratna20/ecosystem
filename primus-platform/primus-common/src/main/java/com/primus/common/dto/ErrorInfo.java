package com.primus.common.dto;

import java.util.Map;

/**
 * Structured error information included in error responses.
 */
public class ErrorInfo {

    private final String code;
    private final String message;
    private final Map<String, Object> details;

    public ErrorInfo(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
}
