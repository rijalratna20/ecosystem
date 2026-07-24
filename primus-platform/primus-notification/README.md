# primus-notification

Event-driven notification service (email, Slack, SMS).

## Overview

`primus-notification` sends triggered notifications to configured channels when key events occur (export completion, approval requests, failures), keeping users informed about data operations.

## Module Status

🔴 **NOT YET IMPLEMENTED (Tier 4 - Phase 1)**

**Target Launch**: Week 10-12 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: HIGH - Improves user experience  

## Critical Requirements

### Entry Criteria (Before Starting)
- ✅ Notification event types and templates approved
- ✅ Depends on: `primus-server` (event source), notification provider APIs

### Exit Criteria (Definition of Done)
- [ ] Email/webhook delivery with retries + DLQ works
- [ ] Delivery tracking APIs exposed

## Purpose

Provides real-time notifications:
- **Export ready**: "Your export is ready for download"
- **Approval pending**: "Your export awaits approval"
- **Failures**: "Export failed: reason"
- **Alerts**: "High-volume exports detected"

## Supported Channels

### Email
- SMTP-based delivery
- Customizable templates
- Retry with exponential backoff

### Slack
- Direct messages to users
- Channel notifications
- Slack app integration

### Webhooks
- HTTP POST to custom endpoints
- JSON payload
- Signature verification

### SMS (Future)
- Twilio/AWS SNS
- Critical alerts only
- Cost-controlled

## Event Types (Planned)

### Export Events
- `EXPORT_INITIATED` — Export started
- `EXPORT_COMPLETED` — Export finished, ready for download
- `EXPORT_FAILED` — Export error with reason

### Approval Events
- `APPROVAL_REQUESTED` — Pending approval
- `APPROVAL_GRANTED` — Approval decision made
- `APPROVAL_REMINDER` — Reminder after 4 hours

### Alert Events
- `HIGH_VOLUME_EXPORTS` — >100 exports/hour
- `STORAGE_QUOTA_WARNING` — >80% storage used
- `SERVICE_DEGRADATION` — Performance issues

## APIs (Planned)

```
# Subscribe to events
POST /api/v1/notifications/subscribe
{
  "event_type": "EXPORT_COMPLETED",
  "channel": "email",
  "email": "user@example.com"
}

# Manage preferences
GET /api/v1/notifications/preferences
POST /api/v1/notifications/preferences
DELETE /api/v1/notifications/preferences/{id}

# Query delivery history
GET /api/v1/notifications/history?days=30
```

## Configuration (Planned)

```properties
# Core
primus.notification.enabled=true

# Email
primus.notification.email.enabled=true
primus.notification.email.smtp-host=smtp.gmail.com
primus.notification.email.smtp-port=587
primus.notification.email.from=primus@example.com
primus.notification.email.from-name=Primus Platform
primus.notification.email.auth-user=${SMTP_USER}
primus.notification.email.auth-password=${SMTP_PASSWORD}

# Slack
primus.notification.slack.enabled=true
primus.notification.slack.token=${SLACK_TOKEN}
primus.notification.slack.default-channel=#data-exports

# Templates
primus.notification.template-path=/etc/primus/templates

# Retry
primus.notification.retry.max-attempts=3
primus.notification.retry.backoff-multiplier=2
primus.notification.retry.initial-delay-ms=1000
```

## Notification Templates (Planned)

### Email: Export Ready
```
Subject: Your export {exportId} is ready

Body:
Hi {userName},

Your export of {applicationName} / {collections} is complete!

Download link: {downloadUrl}
Expires: {expirationDate}

Best,
Primus Platform
```

### Slack: Approval Request
```
{userName} requested export approval.

Application: {applicationName}
Collections: {collections}
Sensitivity: {level}

[APPROVE] [REJECT]
```

### Webhook: Export Completed
```json
{
  "event_id": "evt_123456",
  "type": "EXPORT_COMPLETED",
  "timestamp": "2026-07-23T10:00:00Z",
  "export_id": "exp_123456",
  "application": "CustomerService",
  "collections": ["customers", "accounts"],
  "download_url": "https://primus.example.com/download/exp_123456"
}
```

## Technical Design (Planned)

### Event-Driven Architecture
```
primus-server (event source)
    ↓
primus-notification (event consumer)
    ↓
Delivery Queue (RabbitMQ/Kafka)
    ↓
Channel Adapters
    ├─ Email Sender
    ├─ Slack Messenger
    ├─ Webhook Caller
    └─ SMS Sender (future)
    ↓
External Services (Gmail, Slack, etc.)
```

### Reliability
- Persistent queue
- Retry with exponential backoff
- Dead-letter queue (DLQ) for failed messages
- Delivery status tracking

### Performance
- Async delivery (not blocking export flow)
- Batch sends when applicable
- Rate limiting per channel
- Circuit breaker for failing services

## User Preferences

Users should be able to configure:
- Notification channels (email, Slack, etc.)
- Event types (export complete, approvals, etc.)
- Frequency (immediate, daily digest, etc.)
- Quiet hours (no notifications 9PM-7AM)

## Testing Strategy

### Unit Tests
- Template rendering
- Preference evaluation

### Integration Tests
- Email delivery
- Slack integration
- Webhook callbacks
- Retry behavior
- DLQ handling

### Load Tests
- 1000+ notifications/minute
- Sustained delivery

### Failure Tests
- Email provider down
- Slack API throttling
- Network timeouts
- Invalid credentials

## Module Dependencies

- `primus-common` — shared types
- `primus-server` — event source
- Email provider (SMTP or API)
- Slack API
- Webhook infrastructure

## Deployment Notes

- Requires external email provider (Gmail, SendGrid, etc.)
- Slack app must be installed in workspace
- Webhook endpoints must be accessible and authenticated
- Plan for credential rotation

## Related Modules

- **primus-server** (Core) — Emits events
- **primus-approval** (Tier 2) — Triggers notifications
- **primus-monitoring** (Tier 6) — Tracks delivery metrics

---

**Tier**: 4 (Notifications - Phase 1)  
**Status**: 🔴 NOT STARTED  
**Priority**: HIGH  
**Target Start**: August 25, 2026  
**Target Completion**: September 10, 2026

