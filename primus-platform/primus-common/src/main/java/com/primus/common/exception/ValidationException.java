package com.primus.common.exception;

/** Thrown when request input fails validation. HTTP 400. */
public class ValidationException extends PrimusException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    public ValidationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
