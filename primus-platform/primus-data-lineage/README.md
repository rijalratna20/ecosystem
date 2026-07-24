# primus-data-lineage

Data provenance and lineage tracking service.

## Overview

`primus-data-lineage` tracks data flow from source through export, transformation, retrieval, and archival, providing end-to-end data provenance for compliance and impact analysis.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 8 - Phase 2)**

**Target Launch**: Week 20-24 of Phase 2  
**Phase**: v1.1 (Production Hardening) delivery  
**Priority**: MEDIUM - Enables compliance investigations  

## Purpose

Tracks data lineage:
- **Provenance**: Where did this data come from?
- **Usage**: Who accessed this data and when?
- **Impact**: What will break if schema changes?
- **Compliance**: Audit data flow for regulations

## Key Concepts

### Data Flow Graph
```
Source (Database)
    ↓
Export Operation (collections selected, filters applied)
    ↓
Transformation (masking applied)
    ↓
Storage (NAS/S3)
    ↓
Retrieval (accessed by user)
    ↓
Usage (downloaded, processed)
    ↓
Archive/Deletion
```

### Events Tracked
- `SOURCE_EXTRACTION` — Data extracted from source system
- `EXPORT_INITIATED` — Export started
- `MASKING_APPLIED` — Sensitive fields masked
- `STORED` — Data written to storage
- `RETRIEVED` — Data accessed
- `PROCESSED` — Data used by consumer
- `ARCHIVED` — Data moved to cold storage
- `DELETED` — Data destroyed per retention policy

## APIs (Planned)

```
# Get lineage for a record
GET /api/v1/lineage/records/customer_12345

Response:
{
  "recordId": "customer_12345",
  "source": {
    "system": "customer-db",
    "table": "customers",
    "timestamp": "2026-07-01T00:00:00Z"
  },
  "exports": [
    {
      "exportId": "exp_001",
      "timestamp": "2026-07-01T01:00:00Z",
      "collections": ["customers", "accounts"],
      "masking_applied": true
    }
  ],
  "retrievals": [
    {
      "userId": "user_123",
      "timestamp": "2026-07-01T02:00:00Z",
      "purpose": "UAT testing"
    }
  ]
}

# Get impact analysis
GET /api/v1/lineage/impact?field=customers.ssn

Response:
{
  "field": "customers.ssn",
  "affected_exports": 145,
  "affected_users": 234,
  "dependent_systems": ["reporting", "analytics"]
}

# Query data flow
GET /api/v1/lineage/query?type=DATA_FLOW&from=customer_db&to=s3

# Compliance report
GET /api/v1/lineage/compliance/gdpr?subject=individual_12345
```

## Lineage Graph

### Node Types
- `SOURCE` — Original data source
- `EXPORT` — Export operation
- `TRANSFORMATION` — Masking, filtering
- `STORAGE` — Storage backend
- `RETRIEVAL` — Data accessed
- `USER` — Person accessing data
- `SYSTEM` — Consuming system

### Edge Types
- `EXTRACTED_FROM` — Source → Export
- `TRANSFORMED_BY` — Transformation applied
- `STORED_IN` — Storage location
- `ACCESSED_BY` — User access
- `CONSUMED_BY` — System usage

## Configuration (Planned)

```properties
primus.lineage.enabled=true

# Backend
primus.lineage.backend=neo4j  # Graph database

# Event ingestion
primus.lineage.events.enabled=true
primus.lineage.events.batch-size=100

# Query
primus.lineage.query.max-depth=10  # Stop after 10 hops

# Retention
primus.lineage.retention-days=365
```

## Use Cases

### Query: "Show all accesses of customer data"
```bash
primus lineage query \
  --type DATA_ACCESS \
  --subject customer_12345 \
  --from 2026-01-01 \
  --to 2026-12-31
```

### Query: "Which systems depend on this field?"
```bash
primus lineage impact \
  --field customers.ssn \
  --show-dependent-systems
```

### Compliance: "Generate GDPR compliance report"
```bash
primus lineage compliance \
  --type GDPR \
  --subject-id individual_12345 \
  -o gdpr-report.pdf
```

### Investigation: "Trace data from source to deletion"
```bash
primus lineage trace \
  --record customer_12345 \
  --from source \
  --to deletion \
  --include-all-events
```

## Technical Design (Planned)

### Graph Database
Using Neo4j for lineage graph:
```
(customer_12345:Record)
  -[:EXTRACTED_BY]->(exp_001:Export)
  -[:STORED_IN]->(s3_bucket:Storage)
  -[:ACCESSED_BY]->(user_123:User)
  -[:CONSUMED_BY]->(reporting_system:System)
```

### Event Stream
Events flow from other services:
```
primus-server → Event Bus (Kafka/RabbitMQ)
                    ↓
primus-data-lineage (consumes events)
                    ↓
Neo4j GraphDB (stores lineage)
```

### Query Engine
```
1. Parse lineage query
2. Translate to Cypher (Neo4j query language)
3. Execute graph traversal
4. Format results for API
5. Cache common queries
```

## Use Case Examples

### GDPR Right to Access
```
Query: Show me all data collected about individual_12345
Result: Full lineage from collection to current usage
```

### GDPR Right to Deletion
```
Query: Find all copies of individual_12345 data
Result: All storage locations, systems, and caches
```

### Data Breach Investigation
```
Query: Who had access to SSN data in the last 30 days?
Result: All access events with timestamps and purposes
```

### Field Deprecation Impact
```
Query: What systems will break if we remove field X?
Result: All dependent pipelines and consumers
```

## Performance Targets

- Query latency: <500ms p95
- Lineage capture latency: <100ms
- Graph size: Neo4j supports billions of nodes

## Module Dependencies

- `primus-common` — shared types
- `primus-audit` (Tier 2) — audit events source
- Neo4j database
- Event bus (Kafka/RabbitMQ)

## Testing Strategy

### Unit Tests
- Lineage model creation
- Event processing

### Integration Tests
- End-to-end lineage capture
- Query accuracy
- Compliance report generation

### Performance Tests
- Large graph traversal
- Concurrent queries

## Deployment Notes

- Requires Neo4j instance
- Event stream for data capture
- Sufficient disk space for graph database (plan for growth)
- Regular backup of lineage data

## Related Modules

- **primus-audit** (Tier 2) — Events source
- **primus-server** (Core) — Lineage event emission
- **primus-export** (Core) — Lineage generation

---

**Tier**: 8 (Operations - Phase 2)  
**Status**: 🟠 NOT STARTED  
**Priority**: MEDIUM  
**Target Start**: October 15, 2026  
**Target Completion**: October 29, 2026

