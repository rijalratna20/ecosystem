# primus-storage-s3

Cloud object storage backend (AWS S3, Azure Blob, GCP).

## Overview

`primus-storage-s3` implements multi-cloud storage backend support using S3-compatible APIs, enabling global deployments with replication, backup, and disaster recovery capabilities.

## Module Status

🔴 **NOT YET IMPLEMENTED (Tier 3 - Phase 1)**

**Target Launch**: Week 10-12 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: CRITICAL - Blocks multi-region deployments  

## Critical Requirements

### Entry Criteria (Before Starting)
- ✅ Storage URI/policy contract finalized
- ✅ `primus-storage-core` SPI completed (Tier 3)
- ✅ Depends on: `primus-storage-core`

### Exit Criteria (Definition of Done)
- [ ] Multipart upload + encryption support validated
- [ ] Integration and fallback tests pass
- [ ] All storage backend tests pass

## Purpose

Provides scalable, cloud-native storage:
- **Multi-cloud support**: AWS S3, Azure Blob, GCP
- **Global replication**: Multi-region data distribution
- **Disaster recovery**: Automatic failover
- **Compliance**: Encryption at rest and in transit
- **Cost optimization**: Lifecycle policies, tiering

## Supported Providers

### AWS S3
```properties
primus.storage.s3.provider=aws
primus.storage.s3.bucket=primus-exports
primus.storage.s3.region=us-east-1
primus.storage.s3.access-key=${AWS_ACCESS_KEY}
primus.storage.s3.secret-key=${AWS_SECRET_KEY}
```

### Azure Blob Storage
```properties
primus.storage.s3.provider=azure
primus.storage.s3.container=primus-exports
primus.storage.s3.account-name=${AZURE_ACCOUNT}
primus.storage.s3.account-key=${AZURE_KEY}
```

### Google Cloud Storage
```properties
primus.storage.s3.provider=gcp
primus.storage.s3.bucket=primus-exports
primus.storage.s3.project-id=${GCP_PROJECT}
primus.storage.s3.credentials-file=/etc/gcp/sa.json
```

## Key Features (Planned)

### Multipart Uploads
- Stream large files efficiently
- Automatic retry on transient failures
- Progress tracking

### Encryption
- Server-side encryption at rest (SSE-S3, SSE-KMS)
- TLS for in-transit encryption
- Customer-managed keys (CMK) support

### Lifecycle Policies
```
Day 0-30: Hot storage (immediate access)
Day 31-90: Standard storage
Day 91-365: Glacier (infrequent access)
Day 366+: Deep archive (compliance only)
```

### Replication
- Primary/secondary regions
- Automatic failover
- Cross-region replication
- Version control

### APIs (Planned)

```
# Part of primus-storage-core SPI
StorageProvider interface:
- put(String path, InputStream data)
- get(String path) → InputStream
- delete(String path)
- list(String directory) → List<String>
```

## Storage Structure

```
s3://primus-exports/
├── CustomerService/
│   ├── exp_001/
│   │   ├── customers.json.gz
│   │   ├── accounts.json.gz
│   │   └── metadata.json
├── LoanService/
│   ├── exp_001/
│   │   ├── loans.json.gz
│   │   └── applications.json.gz
```

## Configuration (Planned)

```properties
# Core
primus.storage.backend=s3
primus.storage.s3.enabled=true

# Connection
primus.storage.s3.endpoint=https://s3.amazonaws.com
primus.storage.s3.max-connections=50
primus.storage.s3.connection-timeout=30s

# Performance
primus.storage.s3.multipart-threshold=5mb
primus.storage.s3.multipart-part-size=10mb
primus.storage.s3.thread-pool-size=20

# Encryption
primus.storage.s3.encryption.enabled=true
primus.storage.s3.encryption.algorithm=STANDARD
primus.storage.s3.encryption.kms-key-id=${KMS_KEY_ID}

# Lifecycle
primus.storage.s3.lifecycle.enabled=true
primus.storage.s3.lifecycle.transition-days=30

# Replication
primus.storage.s3.replication.enabled=true
primus.storage.s3.replication.primary-region=us-east-1
primus.storage.s3.replication.secondary-region=us-west-2
```

## Technical Design (Planned)

### Storage Abstraction
- Implement `StorageProvider` interface from `primus-storage-core`
- Support multiple providers via strategy pattern
- Automatic provider selection by bucket

### Resilience
- Exponential backoff retry strategy
- Connection pooling
- Circuit breaker for failed endpoints
- Fallback to NAS/secondary storage

### Performance
- Multipart uploads for large files
- Compression (gzip for JSON)
- Caching of frequently accessed exports
- Parallel downloads

## Comparison: NAS vs. S3

| Feature | NAS | S3 |
|---------|-----|-----|
| **Scalability** | Limited by single device | Unlimited |
| **Global Access** | Requires VPN/replication | Native multi-region |
| **Cost** | CapEx for hardware | OpEx subscription |
| **Availability** | Single point of failure | 99.99% SLA |
| **Setup** | Local mounting | API/CLI |
| **Backup** | Manual | Automatic |

## Migration Path

```
Phase 1 (Current): NAS-only storage
Phase 2 (Tier 3): Support both NAS + S3
Phase 3: S3 primary, NAS fallback
Phase 4: S3-only for new deployments
```

## Testing Strategy

### Unit Tests
- Provider implementation
- Path handling
- Error cases

### Integration Tests
- End-to-end upload/download
- Multipart uploads
- Large file handling

### Performance Tests
- Throughput benchmarks
- Multipart effectiveness
- Concurrent operations

## Deployment Notes

- Requires AWS/Azure/GCP credentials
- Plan for KMS key management
- Set up bucket policies and IAM roles
- Test failover procedures
- Monitor costs (S3 bandwidth can be expensive)

## Module Dependencies

- `primus-common` — shared types
- `primus-storage-core` — SPI interface

## Cost Estimation

**Monthly costs** for 100GB stored, 1TB transferred:
- AWS S3: ~$20-50
- Azure Blob: ~$15-40
- GCP Cloud Storage: ~$15-40

**Storage tiers reduce costs** with lifecycle policies

---

**Tier**: 3 (Storage - Phase 1)  
**Status**: 🔴 NOT STARTED  
**Priority**: CRITICAL  
**Target Start**: August 20, 2026  
**Target Completion**: September 3, 2026

