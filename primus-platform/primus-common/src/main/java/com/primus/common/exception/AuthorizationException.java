package com.primus.common.exception;

/** Thrown when the caller lacks required permissions. HTTP 403. */
public class AuthorizationException extends PrimusException {
    public AuthorizationException(String message) {
        super(message, "AUTHORIZATION_DENIED");
    }
    public AuthorizationException(String message, String errorCode) {
        super(message, errorCode);
    }
}
