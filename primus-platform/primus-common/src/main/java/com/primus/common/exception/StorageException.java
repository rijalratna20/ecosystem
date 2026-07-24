package com.primus.common.exception;

/** Thrown for I/O or storage backend failures. HTTP 500. */
public class StorageException extends PrimusException {
    public StorageException(String message) {
        super(message, "STORAGE_ERROR");
    }
    public StorageException(String message, Throwable cause) {
        super(message, "STORAGE_ERROR", cause);
    }
}
