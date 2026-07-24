# primus-storage-database

Database storage backend adapter (PostgreSQL, MySQL, Oracle).

## Overview

`primus-storage-database` implements a database storage backend for structured data access, enabling efficient queries for metadata, export records, and audit logs through traditional database interfaces.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 3 - Phase 1)**

**Target Launch**: Week 11-12 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Priority**: HIGH - Needed for metadata and audit persistence  

## Purpose

Provides database-backed storage:
- **Metadata registry**: Versioned application metadata
- **Audit events**: High-throughput event logging
- **Export records**: Query export history efficiently
- **Query support**: Structured queries on data

## Database Providers

### PostgreSQL (Recommended)
```properties
primus.storage.database.provider=postgresql
primus.storage.database.host=postgres.example.com
primus.storage.database.port=5432
primus.storage.database.database=primus
primus.storage.database.username=primus
primus.storage.database.password=${DB_PASSWORD}
```

### MySQL/MariaDB
```properties
primus.storage.database.provider=mysql
primus.storage.database.host=mysql.example.com
primus.storage.database.port=3306
primus.storage.database.database=primus
```

### Oracle Database
```properties
primus.storage.database.provider=oracle
primus.storage.database.host=oracle.example.com
primus.storage.database.port=1521
primus.storage.database.sid=primus
```

## Schema

### Metadata Table
```sql
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    version VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    metadata JSONB NOT NULL,
    INDEX idx_name (name),
    INDEX idx_created (created_at)
);
```

### Audit Events Table
```sql
CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID UNIQUE NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    actor_id VARCHAR(255),
    action VARCHAR(100),
    target_type VARCHAR(100),
    target_id VARCHAR(255),
    outcome VARCHAR(50),
    correlation_id VARCHAR(255),
    
    INDEX idx_timestamp (timestamp),
    INDEX idx_actor_id (actor_id),
    INDEX idx_action (action),
    INDEX idx_correlation_id (correlation_id)
);
```

### Exports Table
```sql
CREATE TABLE exports (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    export_id VARCHAR(255) UNIQUE,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    export_count BIGINT,
    
    FOREIGN KEY (application_id) REFERENCES applications(id),
    INDEX idx_status (status),
    INDEX idx_created (created_at)
);
```

## APIs

```java
public interface DatabaseStorageProvider {
    // Metadata queries
    ApplicationMetadata getMetadata(String appName);
    List<ApplicationMetadata> listApplications();
    
    // Audit queries
    List<AuditEvent> queryAuditEvents(AuditQuery query);
    long countAuditEvents(AuditQuery query);
    
    // Export queries
    List<ExportRecord> getExportHistory(String appName, int limit);
    ExportRecord getExportDetails(String exportId);
}
```

## Configuration

```properties
# Connection Pool
primus.storage.database.pool.max-size=20
primus.storage.database.pool.min-size=5
primus.storage.database.pool.idle-timeout=10m
primus.storage.database.pool.connection-timeout=30s

# Queries
primus.storage.database.query.timeout=30s
primus.storage.database.query.max-results=10000

# Transactions
primus.storage.database.transaction.isolation-level=READ_COMMITTED
primus.storage.database.transaction.timeout=5m

# Performance
primus.storage.database.batch-size=100
primus.storage.database.batch-timeout=1s
```

## Migration from NAS to Database

### Schema Creation
```bash
# Use Liquibase/Flyway for versioned migrations
./gradlew :primus-platform:primus-storage-database:dbMigrate
```

### Data Migration
```sql
-- Migrate metadata from files to database
INSERT INTO applications (name, version, metadata, created_at, updated_at)
SELECT app_name, version, metadata_json, created_at, updated_at
FROM legacy_metadata;
```

## Performance Considerations

### Typing & Indexing
```sql
-- Index on frequently queried fields
CREATE INDEX idx_audit_timestamp_actor 
ON audit_events(timestamp, actor_id);

CREATE INDEX idx_exports_app_status 
ON exports(application_id, status);
```

### Query Optimization
- Use prepared statements (prevent SQL injection)
- Batch inserts for high-volume operations
- Partitioning by date for audit events (12 months rolling window)

### Connection Pooling
- HikariCP for efficient connection management
- Tune pool size based on expected concurrency
- Monitor connection leaks

## High Availability

### Replication
```
Primary Database
    ↓ (async replication)
Replica 1 (read-only)
Replica 2 (read-only)
```

### Failover
```
Primary fails
    ↓
Automatic failover to replica
    ↓
Promote replica to primary
    ↓
Re-establish replicas
```

## Testing

### Unit Tests
- Query correctness
- Transaction handling

### Integration Tests
- End-to-end queries
- Data consistency

### Performance Tests
- Query latency benchmarks
- Bulk insert performance
- Concurrent query load

## Module Dependencies

- `primus-common` — shared types
- `primus-storage-core` — SPI interface
- Database driver (postgresql-jdbc, mysql-connector-j, etc.)

## Platforms Tested

| Database | Version | Status | Notes |
|----------|---------|--------|-------|
| **PostgreSQL** | 12+ | ✅ Recommended | Best performance/features |
| **MySQL** | 5.7+ | ✅ Supported | Good performance |
| **MariaDB** | 10.3+ | ✅ Supported | MySQL-compatible |
| **Oracle** | 19c+ | ✅ Supported | Enterprise option |
| **SQLServer** | 2019+ | 🟡 Planned | Future support |

## Operational Considerations

### Backup Strategy
- Daily full backups
- Transaction log backups (for point-in-time recovery)
- Test restore procedures regularly

### Maintenance
- Index defragmentation
- Statistics updates
- Partition management

### Monitoring
- Connection pool health
- Query performance metrics
- Replication lag
- Disk space usage

## Related Modules

- **primus-storage-core** — SPI interface
- **primus-audit** (Tier 2) — Audit event storage
- **primus-metadata** (Core) — Metadata registry

---

**Tier**: 3 (Storage - Phase 1)  
**Status**: 🟠 NOT STARTED  
**Priority**: HIGH  
**Target Start**: August 15, 2026  
**Target Completion**: August 25, 2026

