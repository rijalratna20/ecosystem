# primus-retrieval Module

## Purpose

Inverse of export engine—retrieves previously exported data from storage with support for filtering, pagination, streaming, field masking, and caching. Handles authorization checks and applies role-based field filtering.

## Module Location

```
primus-platform/primus-libs/primus-retrieval/ (Conceptual)
```

## Key Responsibilities

1. **Locate Data**: Find exported data in storage systems
2. **Authenticate**: Verify user permissions
3. **Filter & Transform**: Apply filters and format transformations
4. **Stream**: Return data with pagination/streaming support
5. **Cache**: Optimize frequently accessed exports

## Core Components

### RetrievalService

```java
public interface RetrievalService {
    RetrievalResponse retrieve(RetrievalRequest request);
    Page<Map<String, Object>> retrievePaged(String exportId, int page, int pageSize);
    Stream<Map<String, Object>> retrieveStream(String exportId);
    void validateAccess(String exportId, User user);
}
```

### Retrieval Request

```java
public class RetrievalRequest {
    private String exportId;
    private RetrievalFormat format;    // JSON, CSV, etc.
    private List<String> fields;       // Specific fields only
    private Map<String, Object> filters; // Runtime filters
    private boolean includeMetadata;
    private int pageSize;
}

public enum RetrievalFormat {
    JSON, CSV, XML, PARQUET
}
```

### Retrieval Response

```java
public class RetrievalResponse {
    private String exportId;
    private List<Map<String, Object>> records;
    private long totalRecords;
    private int pageSize;
    private int currentPage;
    private ExportMetadata metadata;
    private String nextPageUrl;
}
```

### Filter Engine

```java
public interface FilterEngine {
    List<Map<String, Object>> apply(
        List<Map<String, Object>> data,
        RetrievalFilter filter
    );
}

public class RetrievalFilter {
    private String field;
    private FilterOperator operator; // eq, ne, gt, lt, in, like
    private Object value;
}

public enum FilterOperator {
    EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN,
    IN, NOT_IN, LIKE, BETWEEN
}
```

## Retrieval Methods

### 1. Simple Retrieval

```java
RetrievalRequest request = new RetrievalRequest()
    .setExportId("exp_20240115_001")
    .setFormat(JSON);

RetrievalResponse response = retrievalService.retrieve(request);
List<Map<String, Object>> records = response.getRecords();
```

### 2. Paginated Retrieval

```java
Page<Map<String, Object>> page = retrievalService.retrievePaged(
    exportId, 
    pageNumber,
    pageSize
);

page.forEach(record -> System.out.println(record));
page.getNextPageUrl(); // Get next page link
```

### 3. Streaming Retrieval

```java
try (Stream<Map<String, Object>> stream = retrievalService.retrieveStream(exportId)) {
    stream
        .filter(record -> (String) record.get("status").equals("ACTIVE"))
        .limit(100)
        .forEach(System.out::println);
}
```

### 4. Field Selection

```java
RetrievalRequest request = new RetrievalRequest()
    .setExportId("exp_20240115_001")
    .setFields(Arrays.asList("userId", "email", "status"));

RetrievalResponse response = retrievalService.retrieve(request);
// Returns only specified fields
```

## Filtering During Retrieval

### Filter Examples

```java
// Single condition
RetrievalFilter filter1 = new RetrievalFilter()
    .setField("status")
    .setOperator(EQUALS)
    .setValue("ACTIVE");

// Range filter
RetrievalFilter filter2 = new RetrievalFilter()
    .setField("createdDate")
    .setOperator(BETWEEN)
    .setValue(Arrays.asList("2024-01-01", "2024-12-31"));

// Pattern matching
RetrievalFilter filter3 = new RetrievalFilter()
    .setField("email")
    .setOperator(LIKE)
    .setValue("%@company.com");

// In list
RetrievalFilter filter4 = new RetrievalFilter()
    .setField("tier")
    .setOperator(IN)
    .setValue(Arrays.asList("PREMIUM", "ENTERPRISE"));
```

## Caching Strategy

### Cache Layers

```java
public class CacheStrategy {
    private static final long CACHE_TTL_MINUTES = 60;
    
    // Memory cache for hot data
    private Map<String, CachedExport> memoryCache;
    
    // Redis cache for distributed deployment
    private RedisCache distributedCache;
    
    public byte[] getOrRetrieve(String exportId) {
        // Check memory cache
        if (memoryCache.contains(exportId)) {
            return memoryCache.get(exportId);
        }
        
        // Check Redis cache
        if (distributedCache.exists(exportId)) {
            byte[] data = distributedCache.get(exportId);
            memoryCache.put(exportId, data);
            return data;
        }
        
        // Retrieve from storage
        byte[] data = storageBackend.retrieve(exportId);
        cacheData(exportId, data);
        return data;
    }
}
```

## Performance Optimizations

- **Connection Pooling**: Reuse storage connections
- **Streaming**: Avoid loading entire dataset
- **Index Usage**: Query optimization
- **Compression**: Decompress only needed portions
- **Parallel Processing**: Multi-threaded field transformation

## Authorization

```java
public class RetrievalAuthorizationService {
    
    @Autowired
    private PermissionService permissionService;
    
    public void validateAccess(String exportId, User user) {
        Export export = exportService.getExport(exportId);
        
        // Check ownership
        if (!export.getCreatedBy().equals(user.getId())) {
            // Check shared permissions
            if (!permissionService.hasPermission(user, export)) {
                throw new AccessDeniedException("No permission to retrieve");
            }
        }
        
        // Check expiration
        if (export.isExpired()) {
            throw new ExportExpiredException();
        }
    }
}
```

## Error Handling

```java
public class RetrievalExceptions {
    public class ExportNotFoundException extends RetrievalException
    public class ExportExpiredException extends RetrievalException
    public class AccessDeniedException extends RetrievalException
    public class StorageException extends RetrievalException
    public class FilterException extends RetrievalException
}
```

## Configuration

```yaml
retrieval:
  # Pagination
  defaultPageSize: 100
  maxPageSize: 1000
  
  # Caching
  cache:
    enabled: true
    ttlMinutes: 60
    maxSize: 1GB
  
  # Streaming
  streamBufferSize: 8192
  
  # Performance
  maxConcurrentRetrievals: 10
  connectionTimeout: 30000
```

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-retrieval:test
```

