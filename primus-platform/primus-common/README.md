# primus-common

Shared data types, exceptions, and utilities for Primus Platform.

## Overview

`primus-common` provides the foundational DTOs, custom exceptions, and utility functions used across all Primus services and libraries.

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-common')
}
```

## Key Components

### DTOs (Data Transfer Objects)
- `ExportRequest` / `ExportResponse` — for export operations
- `RetrievalRequest` / `RetrievalResponse` — for retrieval operations
- `MetadataDTO` — metadata registry entries
- `ApplicationDTO` — application registration
- `UserDTO` — user context

### Exceptions
- `PrimusException` — base exception
- `ValidationException` — input validation failures
- `AuthorizationException` — permission denied
- `StorageException` — storage backend failures
- `MaskingException` — masking rule failures
- `NotFoundException` — resource not found

### Utilities
- `CollectionUtils` — collection helpers
- `StringUtils` — string manipulation
- `DateUtils` — date/time handling
- `JsonUtils` — JSON serialization/deserialization
- `MaskingUtils` — sensitive field masking

## Configuration

No runtime configuration required. All utilities are stateless.

## Testing

```bash
./gradlew :primus-platform:primus-common:test
```

## Module Dependencies

- None (base module, no external Primus dependencies)

## Troubleshooting

**Issue**: JUnit/AssertJ imports not found
- **Solution**: Ensure test dependencies are in build.gradle

## API

### MaskingUtils Example
```java
String masked = MaskingUtils.maskSSN("123-45-6789");
// Result: ***-**-6789
```

### StringUtils Example
```java
String truncated = StringUtils.truncate("very long string", 10);
// Result: "very long "
```

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

