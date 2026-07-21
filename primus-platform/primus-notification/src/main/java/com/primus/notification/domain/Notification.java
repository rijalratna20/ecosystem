package com.primus.notification.domain;

import java.time.Instant;
import java.util.Map;

/** A notification to be delivered to one or more recipients. */
public final class Notification {

    private final String id;
    private final NotificationEventType eventType;
    private final NotificationChannel channel;
    private final String recipient;
    private final String subject;
    private final String body;
    private final Instant createdAt;
    private final Map<String, String> metadata;

    private Notification(Builder b) {
        this.id = b.id;
        this.eventType = b.eventType;
        this.channel = b.channel;
        this.recipient = b.recipient;
        this.subject = b.subject;
        this.body = b.body;
        this.createdAt = b.createdAt;
        this.metadata = b.metadata == null ? Map.of() : Map.copyOf(b.metadata);
    }

    public String getId() { return id; }
    public NotificationEventType getEventType() { return eventType; }
    public NotificationChannel getChannel() { return channel; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Instant getCreatedAt() { return createdAt; }
    public Map<String, String> getMetadata() { return metadata; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String id;
        private NotificationEventType eventType;
        private NotificationChannel channel = NotificationChannel.EMAIL;
        private String recipient;
        private String subject;
        private String body;
        private Instant createdAt = Instant.now();
        private Map<String, String> metadata;

        public Builder id(String v) { this.id = v; return this; }
        public Builder eventType(NotificationEventType v) { this.eventType = v; return this; }
        public Builder channel(NotificationChannel v) { this.channel = v; return this; }
        public Builder recipient(String v) { this.recipient = v; return this; }
        public Builder subject(String v) { this.subject = v; return this; }
        public Builder body(String v) { this.body = v; return this; }
        public Builder createdAt(Instant v) { this.createdAt = v; return this; }
        public Builder metadata(Map<String, String> v) { this.metadata = v; return this; }

        public Notification build() {
            if (id == null) throw new IllegalStateException("id required");
            if (recipient == null || recipient.isBlank()) throw new IllegalStateException("recipient required");
            return new Notification(this);
        }
    }
}
