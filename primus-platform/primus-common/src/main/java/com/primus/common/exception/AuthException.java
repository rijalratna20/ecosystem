package com.primus.common.exception;

/** Thrown when authentication token is missing or invalid. HTTP 401. */
public class AuthException extends PrimusException {
    public AuthException(String message) {
        super(message, "AUTHENTICATION_FAILED");
    }
    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
