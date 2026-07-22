package com.primus.common.exception;

/** Thrown when a resource already exists and cannot be created again. HTTP 409. */
public class ConflictException extends PrimusException {
    public ConflictException(String message) {
        super(message, "CONFLICT");
    }
}
