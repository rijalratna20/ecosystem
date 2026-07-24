# primus-storage-core

Storage provider abstraction layer (SPI).

## Overview

`primus-storage-core` defines the storage provider interface (SPI) that allows Primus to support multiple storage backends (NAS, S3, Azure Blob, GCP) through a common interface, abstracting away backend-specific implementations.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 3 - Phase 1)**

**Target Launch**: Week 10-11 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: HIGH - Blocker for primus-storage-s3  
**Depends on**: Must be completed before primus-storage-s3  

## Purpose

Abstracts storage:
- **Decoupling**: Core services don't know storage backend
- **Interchangeability**: Easy to swap backends
- **Extensibility**: New backends can be added
- **Testing**: Mock storage for unit tests

## Storage Provider Interface (SPI)

```java
public interface StorageProvider {
    /**
     * Store an object (upload/put)
     */
    void put(String path, InputStream data, StorageMetadata metadata)
        throws StorageException;
    
    /**
     * Retrieve an object (download/get)
     */
    InputStream get(String path)
        throws StorageException;
    
    /**
     * Check if object exists
     */
    boolean exists(String path);
    
    /**
     * Delete an object
     */
    void delete(String path)
        throws StorageException;
    
    /**
     * List objects in directory
     */
    List<StorageObject> list(String directory, boolean recursive)
        throws StorageException;
    
    /**
     * Get available capabilities
     */
    StorageCapabilities getCapabilities();
}
```

## Storage Capabilities

```java
public class StorageCapabilities {
    boolean supportsMultipartUpload;
    boolean supportsVersioning;
    boolean supportsEncryption;
    boolean supportsLifecycle;
    boolean supportsReplication;
    long maxObjectSize;    // bytes
    long maxUploadSize;    // bytes per part
}
```

## Storage Models

### StorageObject
```java
class StorageObject {
    String path;           // /app/export/file.json
    long size;             // bytes
    LocalDateTime created;
    LocalDateTime modified;
    String etag;           // integrity check
    Map<String, String> metadata;
}
```

### StorageMetadata
```java
class StorageMetadata {
    String contentType;    // application/json
    String contentEncoding; // gzip
    Map<String, String> tags;
    boolean isPublic;
    StorageClass storageClass;  // HOT, WARM, COLD, ARCHIVED
}
```

## Provider Implementations

### Current (Phase 0)
- ✅ `primus-nas` — NAS/SMB/NFS filesystem

### Planned (Phase 1-2)
- 🟠 `primus-storage-s3` — AWS S3, Azure Blob, GCP (Tier 3)
- 🟠 `primus-storage-database` — PostgreSQL/MySQL (Tier 3)
- 🟡 `primus-storage-cache` — In-memory cache layer (future)
- 🟡 `primus-storage-hybrid` — Multi-backend with routing (future)

## Provider Registry

```java
public class StorageProviderRegistry {
    static void register(String name, Class<? extends StorageProvider> provider);
    static StorageProvider getProvider(String name);
    static StorageProvider create(StorageConfig config);
}

// Usage
StorageProvider nas = StorageProviderRegistry.create(
    new StorageConfig()
        .setBackend("nas")
        .setProperty("path", "/mnt/primus")
);

StorageProvider s3 = StorageProviderRegistry.create(
    new StorageConfig()
        .setBackend("s3")
        .setProperty("bucket", "primus-exports")
        .setProperty("region", "us-east-1")
);
```

## Error Handling

```java
public abstract class StorageException extends PrimusException { }

// Specific exceptions
public class NotFoundException extends StorageException { }
public class AccessDeniedException extends StorageException { }
public class QuotaExceededException extends StorageException { }
public class CorruptedDataException extends StorageException { }
public class TimeoutException extends StorageException { }
```

## Resilience Patterns

### Retry Strategy
```java
StorageProvider provider = new RetryableStorageProvider(
    baseProvider,
    new RetryConfig()
        .maxAttempts(3)
        .backoffMultiplier(2)
        .initialDelayMs(100)
        .maxDelayMs(5000)
);
```

### Circuit Breaker
```java
StorageProvider provider = new CircuitBreakerStorageProvider(
    baseProvider,
    new CircuitBreakerConfig()
        .failureThreshold(10)
        .successThreshold(5)
        .timeout(Duration.ofSeconds(60))
);
```

### Metrics & Monitoring
```java
StorageMetrics metrics = new StorageMetrics();

provider.onSuccess((path, duration) -> 
    metrics.recordSuccess(path, duration)
);

provider.onError((path, error) ->
    metrics.recordError(path, error)
);
```

## Storage URI Format

```
storage://provider/path?params

Examples:
storage://nas/CustomerService/exp_001/customers.json
storage://s3/us-east-1/primus-exports/exp_001/customers.json
storage://gcp/primus-prod/exp_001/customers.json
storage://azure/primus-blob/exp_001/customers.json
```

## Configuration Hierarchy

```
Global defaults
    ↓
Provider-specific defaults
    ↓
Application-specific overrides
    ↓
Request-time parameters
```

Example:
```properties
# Global default
primus.storage.backend=nas

# S3-specific
primus.storage.s3.region=us-east-1

# Application-specific
primus.storage.applications.LoanService.backend=s3
primus.storage.applications.LoanService.s3.region=eu-west-1

# Request-time (if supported)
storage.provider=s3&region=us-west-2
```

## Testing Strategy

### Unit Tests
- Provider interface compliance
- Error handling
- Metrics collection

### Integration Tests
- Mock providers
- Multi-provider routing
- Fallback behavior

### Performance Tests
- Throughput per provider
- Latency comparison
- Concurrent operations

## Module Dependencies

- `primus-common` — shared types

## Implementations (Dependencies for providers)

- **primus-nas**: JCIFS/Java NFS libraries
- **primus-storage-s3**: AWS SDK, Azure SDK, GCP SDK
- **primus-storage-database**: JDBC drivers

## Related Modules

- **primus-nas** (implemented) — NAS provider
- **primus-storage-s3** (Tier 3) — Cloud provider
- **primus-storage-database** (Tier 3) — DB provider
- **primus-export** (Core) — Uses storage SPI
- **primus-retrieval** (Core) — Uses storage SPI

---

**Tier**: 3 (Storage - Phase 1)  
**Status**: 🟠 NOT STARTED  
**Priority**: HIGH  
**Target Start**: August 15, 2026  
**Target Completion**: August 25, 2026

