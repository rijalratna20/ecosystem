# primus-metadata

Metadata registry and versioning service.

## Overview

`primus-metadata` manages the centralized registry of all application metadata (collection definitions, field mappings, relationships, masking rules) with version control and caching.

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-metadata')
}
```

### Retrieve Application Metadata

```java
MetadataRegistry registry = MetadataRegistry.getInstance();

// Get latest metadata for an application
ApplicationContract metadata = registry.getLatest("CustomerService");

// Get specific version
ApplicationContract versionedMetadata = registry.getVersion("CustomerService", "v1.2.3");

// List available versions
List<MetadataVersion> versions = registry.listVersions("CustomerService");
```

### Register/Update Metadata

```java
ApplicationContract contract = ApplicationContract.builder()
    .name("LoanService")
    .version("1.0.0")
    .collections(Arrays.asList(
        Collection.builder().name("loans").build(),
        Collection.builder().name("applications").build()
    ))
    .build();

MetadataVersion registered = registry.register(contract);
System.out.println("Registered: " + registered.getVersion());
```

## Key Features

### Version Management
- Track all metadata changes with timestamps and authors
- Rollback to previous versions if needed
- Semantic versioning support

### Caching
- In-memory cache for frequently accessed metadata
- Configurable TTL (Time-To-Live)
- Cache invalidation on updates

### Validation
- Schema validation on registration
- Consistency checks across collections
- Sensitive field masking rule verification

## Configuration

```properties
primus.metadata.cache.enabled=true
primus.metadata.cache.ttl-minutes=60
primus.metadata.cache.max-size=1000
primus.metadata.validation.strict-mode=true
primus.metadata.storage.backend=postgresql
```

## Testing

```bash
./gradlew :primus-platform:primus-metadata:test
```

## APIs

### MetadataRegistry

```java
ApplicationContract getLatest(String applicationName)
ApplicationContract getVersion(String applicationName, String version)
List<MetadataVersion> listVersions(String applicationName)
MetadataVersion register(ApplicationContract contract)
boolean update(ApplicationContract contract)
boolean delete(String applicationName, String version)
List<String> listApplications()
```

### MetadataCache

```java
void put(String key, ApplicationContract value)
ApplicationContract get(String key)
void invalidate(String key)
void invalidateAll()
CacheStats getStats()
```

## Module Dependencies

- `primus-common`
- `primus-contract`

## Usage Examples

### Listen for Metadata Changes

```java
MetadataRegistry registry = MetadataRegistry.getInstance();

registry.subscribeToChanges(
    new MetadataChangeListener() {
        @Override
        public void onMetadataChanged(String application, MetadataVersion version) {
            System.out.println("Metadata updated: " + application + " -> " + version);
            // Reload affected services
        }
    }
);
```

### Export Metadata Audit Trail

```java
List<MetadataAuditLog> logs = registry.getAuditLog(
    "CustomerService",
    LocalDateTime.now().minusMonths(1),
    LocalDateTime.now()
);

for (MetadataAuditLog log : logs) {
    System.out.println(log.getTimestamp() + " | " + 
        log.getAction() + " | " + 
        log.getAuthor());
}
```

## Troubleshooting

**Issue**: "Metadata not found for application"
- **Solution**: Ensure application is registered with metadata service

**Issue**: "Cache hit rate too low"
- **Solution**: Increase cache size or TTL in configuration

**Issue**: "Validation failed: duplicate collection"
- **Solution**: Ensure collection names are unique within application

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

