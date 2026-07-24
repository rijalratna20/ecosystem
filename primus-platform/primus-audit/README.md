# primus-audit

Immutable audit trail and compliance event logging service.

## Overview

`primus-audit` maintains an immutable, append-only audit trail of all export, retrieval, and administrative operations for compliance and governance purposes.

## Module Status

🔴 **NOT YET IMPLEMENTED (Tier 2 - Phase 1)**

**Target Launch**: Week 8 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: CRITICAL - Blocks production deployment  

## Critical Requirements

### Entry Criteria (Before Starting)
- ✅ Tier 1 stability baseline complete
- ✅ AuditEvent schema approved (actor/action/target/outcome/timestamp/correlationId)
- ✅ Append-only persistence design approved
- ✅ Depends on: `primus-server` (for integration hooks)

### Exit Criteria (Definition of Done)
- [ ] Ingest + query/report APIs implemented
- [ ] End-to-end governance event capture validated
- [ ] Retention policy enforcement verified
- [ ] All Tier 2 exit criteria in strategy.MD satisfied

## Purpose

Provides immutable event logging for:
- **Regulatory compliance**: SOX, HIPAA, GDPR, PCI-DSS
- **Audit trails**: Complete history of data access
- **Governance**: Proof of who accessed what data, when, and why

## Key Features (Planned)

### Audit Event Recording
```
actor: user_123
action: EXPORT_INITIATED
target: CustomerService/customers
outcome: SUCCESS
timestamp: 2026-07-23T10:00:00Z
correlationId: req_abc123
```

### Event Types
- `EXPORT_INITIATED` — Export started
- `EXPORT_COMPLETED` — Export finished
- `EXPORT_FAILED` — Export error
- `RETRIEVAL_REQUESTED` — Data retrieval request
- `RETRIEVAL_COMPLETED` — Data retrieval finished
- `APPLICATION_REGISTERED` — App registration
- `METADATA_UPDATED` — Metadata change
- `PERMISSION_DENIED` — Access denied event

### APIs (Planned)

```
# Record event (internal use only)
POST /api/v1/audit/events
Headers: X-Internal-Service: true

# Query audit trail
GET /api/v1/audit/exports?from=2026-01-01&to=2026-12-31
GET /api/v1/audit/retrievals?userId=user_123
GET /api/v1/audit/actions?action=EXPORT_INITIATED

# Generate compliance reports
GET /api/v1/audit/reports/hipaa
GET /api/v1/audit/reports/sox
GET /api/v1/audit/reports/gdpr
```

## Technical Design (Planned)

### Persistence
- Append-only event store (no deletes, no updates)
- Immutable sequential event ID
- Indexed by timestamp, actor, action, target
- Partitioned by date for retention policy

### Retention Policy
```
90 days: Keep all events
91-365 days: Archive to cold storage
1+ years: Delete per compliance policy
```

### Performance Targets
- Event ingestion: <10ms p50, <50ms p95
- Query: <500ms p95 for typical queries
- Throughput: 10,000+ events/second

## Module Dependencies

- `primus-common` — shared types
- `primus-server` — event source integration
- PostgreSQL (or other append-only DB)

## Implementation Phases

### Phase 1: Core Event Store
- [ ] Implement append-only persistence
- [ ] Event schema and models
- [ ] Ingest API

### Phase 2: Query & Reporting
- [ ] Query APIs by actor/action/target
- [ ] Report generation (HIPAA, SOX, GDPR)
- [ ] UI integration

### Phase 3: Retention & Archival
- [ ] Retention policy enforcement
- [ ] Archive to cold storage
- [ ] Restore procedures

## Testing Strategy

### Unit Tests
- Event model validation
- Schema correctness
- Immutability enforcement

### Integration Tests
- End-to-end event capture from primus-server
- Query accuracy
- Retention policy execution

### Performance Tests
- Ingestion throughput benchmark
- Query latency under load
- Storage efficiency

## Configuration (Planned)

```properties
primus.audit.enabled=true
primus.audit.storage.backend=postgresql
primus.audit.storage.partition-by=date

# Retention
primus.audit.retention.hot-days=90
primus.audit.retention.archive-days=365
primus.audit.retention.archive-location=s3://primus-archive

# Performance
primus.audit.batch-size=1000
primus.audit.flush-interval-ms=5000
```

## Compliance Mapping

| Regulation | Required Events |
|-----------|-----------------|
| **SOX** | All export/retrieval + admin actions |
| **HIPAA** | PHI access with reason codes |
| **GDPR** | Data access by data subject ID |
| **PCI-DSS** | Credit card data access |

## Deployment Notes

- Deploy before primus-approval (Tier 2)
- Requires database with append-only support
- Plan for archive storage if > 1 year retention
- Set up automated retention policy jobs

## Related Modules

- **primus-approval** (Tier 2) — Uses audit events
- **primus-server** (Core) — Provides events
- **primus-data-lineage** (Tier 8) — Consumes events

---

**Tier**: 2 (Governance - Phase 1)  
**Status**: 🔴 NOT STARTED  
**Priority**: CRITICAL  
**Target Start**: August 1, 2026  
**Target Completion**: August 15, 2026

