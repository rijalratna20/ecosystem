package com.primus.common.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception for all Primus platform errors.
 */
public abstract class PrimusException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> details = new HashMap<>();

    protected PrimusException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected PrimusException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }

    public Map<String, Object> getDetails() { return Collections.unmodifiableMap(details); }

    public PrimusException withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}
