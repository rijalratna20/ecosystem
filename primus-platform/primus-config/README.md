# primus-config

Centralized configuration management with feature flags.

## Overview

`primus-config` provides centralized, versioned configuration management separate from code, enabling dynamic changes, feature flags, and environment-specific settings without redeployment.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 7 - Phase 2)**

**Target Launch**: Week 16-20 of Phase 2  
**Phase**: v1.1 (Production Hardening) delivery  
**Priority**: MEDIUM - Enables configuration governance  

## Purpose

Centralizes configuration:
- **Separation of concerns**: Config separate from code
- **Dynamic updates**: Change config without restart
- **Feature flags**: Staged rollouts and experimentation
- **Audit trail**: Track all configuration changes
- **Environment isolation**: Dev/test/prod configs

## Key Features (Planned)

### Hierarchical Configuration
```
global.properties
├─ application.name
└─ database.pool-size

environment/dev.properties
├─ database.url (dev db)
└─ log.level (DEBUG)

environment/prod.properties
├─ database.url (prod db)
└─ log.level (INFO)

secret/prod/passwords.properties (encrypted)
```

### Feature Flags
```
FEATURE_AUDIT_ENABLED: true
FEATURE_APPROVAL_ENABLED: false  (roll out tomorrow)
FEATURE_S3_STORAGE_ENABLED: true (5% of traffic)
```

### APIs (Planned)

```
# Get configuration
GET /api/v1/config/primus.audit.enabled

# List all config
GET /api/v1/config

# Update configuration
POST /api/v1/config
{
  "key": "primus.rate-limit.per-minute",
  "value": "100",
  "environment": "prod",
  "appliesAt": "2026-07-24T10:00:00Z"
}

# Get feature flag status
GET /api/v1/features/FEATURE_S3_STORAGE_ENABLED

# Toggle feature flag
POST /api/v1/features/FEATURE_S3_STORAGE_ENABLED/toggle
{
  "enabled": true,
  "rollout_percentage": 10,
  "notes": "Gradual rollout"
}

# Get configuration history
GET /api/v1/config/history?key=primus.rate-limit.per-minute
```

## Configuration (Planned)

```properties
primus.config.enabled=true

# Backend
primus.config.backend=postgresql

# Caching
primus.config.cache.enabled=true
primus.config.cache.ttl-seconds=60

# Audit
primus.config.audit.enabled=true

# Secret encryption
primus.config.encryption.enabled=true
primus.config.encryption.algorithm=AES-256
primus.config.encryption.key-provider=vault  # HashiCorp Vault
```

## Example Usage

### Reading Configuration
```java
ConfigServer configServer = ConfigServer.getInstance();

// Get single value
String rateLimit = configServer.getString("primus.rate-limit.per-minute");

// Get with default
String maxConnections = configServer.getString(
    "database.max-connections",
    "20"  // default
);

// Get all config for application
Map<String, String> allConfig = configServer.getConfiguration();
```

### Using Feature Flags
```java
FeatureFlagService flags = FeatureFlagService.getInstance();

if (flags.isEnabled("FEATURE_S3_STORAGE_ENABLED")) {
    // Use S3 storage
    storage = new S3StorageProvider();
} else {
    // Use NAS storage
    storage = new NasStorageProvider();
}
```

## Feature Flags Pattern

### Simple Boolean
```json
{
  "name": "FEATURE_AUDIT_ENABLED",
  "enabled": true,
  "rollout_percentage": 100
}
```

### Gradual Rollout
```json
{
  "name": "FEATURE_S3_STORAGE_ENABLED",
  "enabled": true,
  "rollout_percentage": 10,
  "rollout_user_ids": ["user_123", "user_456"],
  "schedule": {
    "monday": 20,
    "wednesday": 50,
    "friday": 100
  }
}
```

### A/B Testing
```json
{
  "name": "FEATURE_NEW_EXPORT_UI",
  "enabled": true,
  "variants": {
    "control": {"rollout_percentage": 50},
    "treatment": {"rollout_percentage": 50}
  }
}
```

## Technical Design (Planned)

### Architecture
```
ConfigServer
│
├─ ConfigProvider (reads from backend)
│   ├─ PostgreSQL provider
│   ├─ Consul provider
│   └─ Environment variable provider
│
├─ ConfigCache (in-memory cache with TTL)
│
├─ FeatureFlagEvaluator (evaluate flags for user/request)
│
└─ ConfigAuditLog (track all changes)
```

### Change Flow
```
Admin: Update config via API
    ↓
ConfigServer validates + audits
    ↓
Database updated (versioned)
    ↓
Cache invalidated
    ↓
Listeners notified (subscribers)
    ↓
Services reload configuration
```

### Change Notifications
```java
configServer.subscribe("primus.rate-limit.*", (key, oldValue, newValue) -> {
    System.out.println("Config changed: " + key + " = " + newValue);
    // Re-initialize rate limiter
});
```

## Encryption

Sensitive values (passwords, API keys) are encrypted:
```properties
# In file (encrypted)
database.password=ENC(lj12i3h4j5k6l7m8n9o0)

# In memory (decrypted)
database.password=actual_password
```

## Deployment Notes

- Requires database for config storage
- Supports HashiCorp Vault for secret management
- Can fall back to environment variables
- Multi-instance coordinator (leader election)
- Hot reload without restart (most changes)
- Graceful degradation if config unavailable

## Testing Strategy

### Unit Tests
- Config parsing
- Feature flag evaluation
- Cache behavior

### Integration Tests
- End-to-end config updates
- Subscriber notifications
- Encryption/decryption

### Load Tests
- High read frequency
- Concurrent config updates

## Module Dependencies

- `primus-common` — shared types
- Database backend (PostgreSQL)
- Secret store (HashiCorp Vault, optional)

## Related Modules

- **primus-server** (Core) — Consumes config
- **primus-monitoring** (Tier 6) — Tracks config changes

---

**Tier**: 7 (Configuration - Phase 2)  
**Status**: 🟠 NOT STARTED  
**Priority**: MEDIUM  
**Target Start**: September 30, 2026  
**Target Completion**: October 14, 2026

