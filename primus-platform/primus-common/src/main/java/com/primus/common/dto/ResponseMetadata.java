package com.primus.common.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Metadata attached to every API response (timestamp, request ID).
 */
public class ResponseMetadata {

    private final Instant timestamp;
    private final String requestId;

    public ResponseMetadata(Instant timestamp, String requestId) {
        this.timestamp = timestamp;
        this.requestId = requestId;
    }

    public Instant getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }

    public static ResponseMetadata now() {
        return new ResponseMetadata(Instant.now(), UUID.randomUUID().toString());
    }

    public static ResponseMetadata of(String requestId) {
        return new ResponseMetadata(Instant.now(), requestId);
    }
}
