# Storage Architecture

## Overview

Primus supports multiple storage backends for persisting exported data and retrieval caches.

## Storage Backends

### NAS (Network-Attached Storage)

**Use Case**: Primary storage for large data exports
- **Module**: `primus-nas`
- **Configuration**: Network path, authentication credentials
- **Advantages**: Cost-effective, high capacity, archival support
- **Access Pattern**: File-based access via SMB/NFS

```yaml
storage:
  type: NAS
  host: nas.company.com
  path: /exports
  credentials: ${NAS_CREDENTIALS}
  retention: 90d
```

### Cloud Storage

**Use Case**: Scalable, distributed storage
- **Providers**: AWS S3, Azure Blob, GCP Cloud Storage
- **Configuration**: Bucket name, region, credentials
- **Advantages**: Global accessibility, automatic replication
- **Access Pattern**: Object storage API

```yaml
storage:
  type: S3
  bucket: primus-exports
  region: us-east-1
  encryption: AES256
  credentials: ${AWS_CREDENTIALS}
```

### Database Storage

**Use Case**: Structured data and metadata
- **Backends**: PostgreSQL, MySQL, MongoDB
- **Configuration**: Connection string, schema
- **Advantages**: ACID transactions, query support
- **Access Pattern**: SQL/NoSQL queries

```yaml
storage:
  type: PostgreSQL
  host: db.company.com
  port: 5432
  database: primus
  credentials: ${DB_CREDENTIALS}
```

## Storage Configuration

### Multi-Storage Setup

```yaml
storage:
  default: nas
  backends:
    nas:
      enabled: true
      type: NAS
      host: nas.company.com
    s3:
      enabled: true
      type: S3
      bucket: primus-exports
    postgres:
      enabled: true
      type: PostgreSQL
      connection: jdbc:postgresql://localhost:5432/primus
```

## Retention Policies

- **Hot Storage**: Recent exports, immediate access
- **Warm Storage**: Archive storage after 30 days
- **Cold Storage**: Long-term retention, infrequent access
- **Deletion**: Automatic cleanup after retention period

## Data Integrity

- **Checksums**: MD5, SHA256 verification
- **Replication**: Multi-region backup
- **Encryption**: In-transit and at-rest
- **Audit Logging**: Track all access and modifications

