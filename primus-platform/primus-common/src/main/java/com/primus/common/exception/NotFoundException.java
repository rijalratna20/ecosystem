package com.primus.common.exception;

/** Thrown when a requested resource cannot be found. HTTP 404. */
public class NotFoundException extends PrimusException {
    public NotFoundException(String resourceType, String id) {
        super(resourceType + " not found: " + id, "NOT_FOUND");
        withDetail("resourceType", resourceType);
        withDetail("id", id);
    }
}
