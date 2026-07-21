package com.primus.gateway.filter;

/**
 * Represents the outcome of a gateway filter chain evaluation.
 */
public final class FilterResult {
    private final boolean allowed;
    private final int httpStatusCode;
    private final String message;

    private FilterResult(boolean allowed, int httpStatusCode, String message) {
        this.allowed = allowed;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public static FilterResult allow() {
        return new FilterResult(true, 200, null);
    }

    public static FilterResult deny(int statusCode, String message) {
        return new FilterResult(false, statusCode, message);
    }

    public boolean isAllowed() { return allowed; }
    public int getHttpStatusCode() { return httpStatusCode; }
    public String getMessage() { return message; }
}
