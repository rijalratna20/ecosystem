package com.primus.server.audit;

import java.time.Instant;

/**
 * An immutable audit event record.
 */
public class AuditEvent {

    private final String eventId;
    private final String action;
    private final String actor;
    private final String appId;
    private final String targetId;
    private final String correlationId;
    private final Instant timestamp;
    private final String outcome;

    public AuditEvent(String eventId, String action, String actor, String appId,
                      String targetId, String correlationId, String outcome) {
        this.eventId = eventId;
        this.action = action;
        this.actor = actor;
        this.appId = appId;
        this.targetId = targetId;
        this.correlationId = correlationId;
        this.timestamp = Instant.now();
        this.outcome = outcome;
    }

    public String getEventId() { return eventId; }
    public String getAction() { return action; }
    public String getActor() { return actor; }
    public String getAppId() { return appId; }
    public String getTargetId() { return targetId; }
    public String getCorrelationId() { return correlationId; }
    public Instant getTimestamp() { return timestamp; }
    public String getOutcome() { return outcome; }
}
