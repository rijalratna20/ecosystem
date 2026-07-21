package com.primus.audit.domain;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable record of a single auditable action.
 * Once persisted, audit events must never be modified or deleted (append-only).
 */
public final class AuditEvent {

    private final String id;
    private final AuditEventType type;
    private final String principalId;
    private final String applicationId;
    private final String resourceId;
    private final Instant occurredAt;
    private final String remoteAddress;
    private final Map<String, String> metadata;
    private final String outcome;

    private AuditEvent(Builder b) {
        this.id = b.id;
        this.type = b.type;
        this.principalId = b.principalId;
        this.applicationId = b.applicationId;
        this.resourceId = b.resourceId;
        this.occurredAt = b.occurredAt;
        this.remoteAddress = b.remoteAddress;
        this.metadata = b.metadata == null ? Map.of() : Map.copyOf(b.metadata);
        this.outcome = b.outcome;
    }

    public String getId() { return id; }
    public AuditEventType getType() { return type; }
    public String getPrincipalId() { return principalId; }
    public String getApplicationId() { return applicationId; }
    public String getResourceId() { return resourceId; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getRemoteAddress() { return remoteAddress; }
    public Map<String, String> getMetadata() { return metadata; }
    public String getOutcome() { return outcome; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String id;
        private AuditEventType type;
        private String principalId;
        private String applicationId;
        private String resourceId;
        private Instant occurredAt = Instant.now();
        private String remoteAddress;
        private Map<String, String> metadata;
        private String outcome;

        public Builder id(String id) { this.id = id; return this; }
        public Builder type(AuditEventType type) { this.type = type; return this; }
        public Builder principalId(String principalId) { this.principalId = principalId; return this; }
        public Builder applicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder occurredAt(Instant occurredAt) { this.occurredAt = occurredAt; return this; }
        public Builder remoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }
        public Builder outcome(String outcome) { this.outcome = outcome; return this; }

        public AuditEvent build() {
            if (id == null) throw new IllegalStateException("id is required");
            if (type == null) throw new IllegalStateException("type is required");
            return new AuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "AuditEvent{id='" + id + "', type=" + type + ", principal='" + principalId
                + "', app='" + applicationId + "', at=" + occurredAt + '}';
    }
}
