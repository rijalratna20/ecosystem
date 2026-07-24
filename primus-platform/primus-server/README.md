# primus-server

Core orchestration service for Primus Platform.

## Overview

`primus-server` is the main service that orchestrates export, retrieval, storage, and API operations. It integrates registration, security, metadata, and storage components into a cohesive platform service.

## Quick Start

### Build and Run

```bash
cd primus-platform
./gradlew :primus-platform:primus-server:build

# Run locally
./gradlew :primus-platform:primus-server:bootRun

# Or run the JAR
java -jar primus-server-boot.jar
```

### Configuration

```properties
# Server
server.port=8080
server.servlet.context-path=/primus

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/primus
spring.datasource.username=primus
spring.datasource.password=primus_password

# Authentication
primus.auth.jwt.secret-key=your-secret-key
primus.auth.jwt.expiration=3600

# Storage
primus.storage.backend=nas
primus.storage.nas.path=/data/exports

# Metadata
primus.metadata.cache.enabled=true
primus.metadata.cache.ttl-minutes=60
```

## Key APIs

### Export Endpoint
```
POST /api/v1/exports
Headers: Authorization: Bearer <token>
Body: {
  "application": "CustomerService",
  "collections": ["customers", "accounts"],
  "filter": "region = 'US'"
}
Response: {
  "exportId": "exp_123456",
  "status": "QUEUED",
  "createdAt": "2026-07-23T10:00:00Z"
}
```

### Retrieval Endpoint
```
GET /api/v1/exports/{exportId}/data
Headers: Authorization: Bearer <token>
Response: {
  "data": {...},
  "status": "COMPLETED",
  "retrievedAt": "2026-07-23T10:05:00Z"
}
```

### Metadata Endpoint
```
GET /api/v1/applications/{appName}/metadata
Headers: Authorization: Bearer <token>
Response: {
  "application": "CustomerService",
  "version": "1.0.0",
  "collections": [...]
}
```

### Application Registration
```
POST /api/v1/applications
Headers: Authorization: Bearer <token>
Body: {
  "name": "NewService",
  "metadata": {...}
}
Response: {
  "applicationId": "app_123456",
  "status": "REGISTERED"
}
```

## Packages

### Export Package
- `com.primus.export.controller` — REST endpoints
- `com.primus.export.service` — business logic
- `com.primus.export.model` — domain models
- `com.primus.export.persistence` — data access

### Retrieval Package
- `com.primus.retrieval.controller` — REST endpoints
- `com.primus.retrieval.service` — business logic
- `com.primus.retrieval.model` — domain models

### Core Packages
- `com.primus.registration` — application registration
- `com.primus.security` — auth/authz
- `com.primus.metadata` — metadata management
- `com.primus.storage` — storage abstraction

## Testing

```bash
./gradlew :primus-platform:primus-server:test

# Use test profile
./gradlew :primus-platform:primus-server:bootRun --args='--spring.profiles.active=test'
```

## Health Check

```
GET /actuator/health
Response: {
  "status": "UP",
  "components": {
    "database": {"status": "UP"},
    "storage": {"status": "UP"},
    "cache": {"status": "UP"}
  }
}
```

## Module Dependencies

- `primus-common`
- `primus-contract`
- `primus-annotations`
- `primus-auth-client`
- `primus-metadata`
- `primus-plugin`
- `primus-sdk`

## Cross-Cutting Concerns

### Audit (Planned - Tier 2)
- Tracks all export and retrieval operations
- Records actor, action, timestamp, outcome
- Provides audit trail queries

### Approval (Planned - Tier 2)
- Gates sensitive exports
- Multi-level approval workflows
- SLA enforcement

### Notification (Planned - Tier 4)
- Event-driven notifications
- Email, Slack, SMS channels
- Delivery tracking

### Monitoring (Planned - Tier 6)
- Performance metrics
- Health checks
- Alerting

## Troubleshooting

**Issue**: "Cannot connect to database"
- **Solution**: Verify database connection string and credentials

**Issue**: "Unauthorized" on API calls
- **Solution**: Ensure valid JWT token is provided in Authorization header

**Issue**: "Application not registered"
- **Solution**: Register application with POST /api/v1/applications first

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Core Service  
**Last Updated**: July 2026

