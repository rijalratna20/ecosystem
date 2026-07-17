# primus-common Module

## Purpose

Foundation library providing shared data types, utilities, exceptions, and constants used across all Primus modules. No external business logic—just reusable components.

## Responsibilities

✓ **Common DTOs**
- ApiResponse wrapper
- Pagination models
- Filter/sort specifications
- Metadata objects

✓ **Exceptions**
- PrimusException (base)
- ValidationException
- AuthException
- StorageException
- NotFound/AlreadyExists

✓ **Utilities**
- String utilities (camelCase, snake_case, validation)
- Date utilities (formatting, parsing, UTC conversion)
- Collection utilities (partition, flatten, merge)
- Type conversions

✓ **Logging**
- Audit logging framework
- Event tracking
- Contextual logging

✓ **Constants**
- API versions
- Default values
- Timeout configurations
- Size limits

## Public APIs

### Response Models

```java
// Standard API response wrapper
public class ApiResponse<T> {
    private String status;              // SUCCESS, ERROR
    private int code;                   // HTTP status
    private T data;                     // Response body
    private ErrorInfo error;            // Error details (if error)
    private Metadata metadata;          // Request metadata
}

public class Metadata {
    private Instant timestamp;
    private String requestId;
    private Map<String, Object> custom;
}

// Pagination support
public class Page<T> {
    private List<T> content;
    private long totalElements;
    private int pageSize;
    private int pageNumber;
    private boolean hasNext;
    private int totalPages;
}

// Sorting
public class Sort {
    private String field;
    private Direction direction;  // ASC, DESC
}

// Filtering
public class Filter {
    private String field;
    private Operator operator;    // EQ, NE, GT, LT, IN, LIKE
    private Object value;
}
```

### Exception Hierarchy

```java
public abstract class PrimusException extends RuntimeException {
    protected String errorCode;
    protected Map<String, Object> details;
    
    public PrimusException(String message, String errorCode)
    public Map<String, Object> getDetails()
}

// Specific exceptions
public class ValidationException extends PrimusException        // 400
public class AuthException extends PrimusException             // 401
public class AuthorizationException extends PrimusException    // 403
public class NotFoundException extends PrimusException         // 404
public class ConflictException extends PrimusException         // 409
public class StorageException extends PrimusException          // 500
public class TimeoutException extends PrimusException          // 504
```

### Utilities

```java
// String
StringUtils.toCamelCase("user_profile")           // userProfile
StringUtils.toSnakeCase("UserProfile")            // user_profile
StringUtils.toKebabCase("UserProfile")            // user-profile
StringUtils.isValidEmail("user@example.com")      // true
StringUtils.isValidUuid(str)                      // boolean

// Date
DateUtils.now()                                   // LocalDateTime.now(UTC)
DateUtils.toUtc(localDateTime)                    // Convert to UTC
DateUtils.formatDate(date, "yyyy-MM-dd")          // Format date
DateUtils.parseDate("2024-01-01", "yyyy-MM-dd")   // Parse date
DateUtils.addDays(date, 7)                        // Add days
DateUtils.expiresIn(date, durationMs)             // Check expiration

// Collection
CollectionUtils.partition(list, 100)              // List<List<T>>
CollectionUtils.flatten(listOfLists)              // Flatten nested
CollectionUtils.merge(map1, map2)                 // Merge maps
CollectionUtils.chunked(list, 50)                 // Batch process
```

## Packages

```
com.primus.common
├── dto/                  # Data Transfer Objects
│   ├── ApiResponse
│   ├── Page<T>
│   ├── Filter
│   ├── Sort
│   └── Metadata
├── exception/            # Exception hierarchy
│   ├── PrimusException
│   ├── ValidationException
│   ├── AuthException
│   ├── NotFoundException
│   └── StorageException
├── util/                 # Utilities
│   ├── StringUtils
│   ├── DateUtils
│   ├── CollectionUtils
│   └── TypeUtils
├── constant/             # Constants
│   ├── ApiConstants
│   ├── PrimusConstants
│   └── TimeoutConstants
└── logging/              # Logging
    ├── AuditLogger
    ├── EventLog
    └── RequestContext
```

## Dependencies

- **None on Primus modules**
- External:
  - Lombok (annotations)
  - SLF4J (logging)
  - Jackson (JSON)
  - Java 17 stdlib

## Configuration

No configuration required. This is a pure library module with no beans or properties.

## Sequence

**primus-common** is a leaf dependency—all modules depend on it, nothing depends on primus-common (except tests).

```
All Modules
    │
    ├── primus-utils
    ├── primus-core
    ├── primus-export
    ├── primus-retrieval
    ├── primus-security
    │
    └──→ primus-common ◄─ (No upstream deps)
```

## Extension Points

### Adding New DTOs

```java
// Location: com.primus.common.dto

@Data
@Builder
public class MyModel {
    private String id;
    private LocalDateTime createdAt;
    
    // Implement if needed
    public static MyModel fromEntity(MyEntity entity) { }
}
```

### Adding New Utilities

```java
// Location: com.primus.common.util

public class MyUtils {
    private static final Logger logger = LoggerFactory.getLogger(MyUtils.class);
    
    public static String transform(String input) {
        // Implement
    }
    
    // Add unit tests in src/test/java
}
```

### Adding New Exceptions

```java
// Location: com.primus.common.exception

public class MyException extends PrimusException {
    public MyException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    public MyException withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}
```

## Future Enhancements

- [ ] Add caching annotation (`@Cacheable`)
- [ ] Add validation annotations (`@ValidEmail`, etc.)
- [ ] Add serialization annotations for custom formats
- [ ] Add metrics annotations (`@Timed`, `@Counted`)
- [ ] Add circuit breaker patterns
- [ ] Add retry configuration objects
- [ ] Distributed tracing context support
- [ ] Add internationalization (i18n) keys

## Usage Example

```java
// In another module
@Autowired
private ExportService exportService;

try {
    Export export = exportService.create(request);
    return ApiResponse.success(export);
} catch (ValidationException e) {
    logger.warn("Validation failed: {}", e.getErrorCode());
    return ApiResponse.error(e.getErrorCode(), e.getMessage(), 400);
} catch (PrimusException e) {
    logger.error("Unexpected error: {}", e.getErrorCode(), e);
    return ApiResponse.error(e.getErrorCode(), e.getMessage(), 500);
}
```

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-common:test
```

Unit tests cover:
- Exception creation and details
- String utilities edge cases
- Date parsing/formatting
- Collection utilities with nulls/empties
- Pagination boundary conditions

## Build & Publish

```bash
# Build
./gradlew :primus-platform:primus-libs:primus-common:build

# Publish to local Maven
./gradlew :primus-platform:primus-libs:primus-common:publishToMavenLocal
```

---

**Related:**
- [primus-utils](./primus-utils.md) - Builds on primus-common
- [02-gradle-modules.md](../02-gradle-modules.md) - Module dependencies

