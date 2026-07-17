# Event Schemas

This document shows example event payloads used by the Primus Event Bus. Events are JSON objects with well-known fields. Backends (Kafka/Rabbit/Redis Streams) can be plugged in.

## ExportCompleted

```json
{
  "eventType": "ExportCompleted",
  "exportId": "exp_20260716_001",
  "application": "LoanService",
  "contract": "UserProfile",
  "version": "1.0",
  "recordCount": 12000,
  "fileSizeBytes": 53421900,
  "storage": {
    "backend": "nas",
    "path": "/exports/LoanService/2026/07/16/exp_20260716_001"
  },
  "timestamp": "2026-07-16T12:34:56Z"
}
```

## MetadataRegistered

```json
{
  "eventType": "MetadataRegistered",
  "application": "LoanService",
  "contract": "LoanAccount",
  "version": "2.1",
  "registeredBy": "alice@example.com",
  "timestamp": "2026-07-16T12:00:00Z"
}
```

## ApprovalRequested

```json
{
  "eventType": "ApprovalRequested",
  "approvalId": "apr_20260716_001",
  "exportId": "exp_20260716_001",
  "application": "LoanService",
  "requestedBy": "bob@example.com",
  "sensitivity": "HIGH",
  "timestamp": "2026-07-16T12:01:00Z"
}
```

## Notes
- All events MUST include `eventType` and `timestamp`.
- Events should be schema-validated before publishing; consider using JSON Schema or Avro for strict typing.
- Events that carry sensitive information must not contain PII — include identifiers referencing audit records instead.

