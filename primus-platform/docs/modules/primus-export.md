# primus-export Module

## Purpose

Core export engine for extracting, validating, transforming, and storing data according to contract definitions. Handles the complete export pipeline from query to persistent storage.

## Responsibilities

✓ **Export Query Processing**
- Parse export requests (metadata.yaml)
- Validate against contract schema
- Check application permissions
- Resolve collections and filters

✓ **Data Retrieval**
- Query source systems
- Apply field-level filtering
- Paginate large result sets
- Handle nested objects/collections

✓ **Validation Pipeline**
- Validate data against contract
- Type checking
- Pattern validation (regex)
- Range validation (min/max)
- Enum validation
- Custom validators

✓ **Data Transformation**
- Serialize to output format (JSON, CSV, Parquet)
- Apply custom field transformers
- Handle sensitive data masking
- Format currency/dates
- Resolve object relationships

✓ **Storage Operations**
- Select appropriate backend (NAS/S3/DB)
- Compress output (optional)
- Calculate checksums
- Store metadata
- Track retention policies

✓ **Error Handling**
- Collect validation errors
- Report error line numbers
- Provide detailed error context
- Support partial failures (some records, not all)

## Public APIs

### ExportService

```java
public interface ExportService {
    // Initiate export
    Export initiateExport(ExportRequest request);
    
    // Get export details
    Export getExport(String exportId);
    
    // List exports with filtering
    Page<Export> listExports(ExportFilter filter);
    
    // Get real-time status
    ExportStatus getExportStatus(String exportId);
    
    // Cancel ongoing export
    void cancelExport(String exportId);
    
    // Delete export (and data)
    void deleteExport(String exportId);
}
```

### ExportRequest

```java
public class ExportRequest {
    private String contractName;           // Required
    private String contractVersion;        // Optional (uses latest)
    private Map<String, Object> filters;   // Optional
    private ExportFormat format;           // JSON, CSV, PARQUET
    private String destination;            // nas, s3, postgres
    private boolean async;                 // Async or blocking
    private boolean compress;              // Gzip output
    private Map<String, String> options;   // Custom options
}

public enum ExportFormat {
    JSON, CSV, PARQUET, XML, AVRO
}
```

### Export

```java
public class Export {
    private String exportId;               // Unique ID
    private String contractName;           // Which contract
    private String contractVersion;        // Contract version
    private ExportStatus status;           // PENDING/PROCESSING/COMPLETED
    private long recordCount;              // Total records
    private long recordsFailed;            // Failed records
    private long fileSize;                 // Bytes
    private String checksum;               // SHA256
    private LocalDateTime createdAt;       // Start time
    private LocalDateTime completedAt;     // End time
    private String createdBy;              // User/application
    private LocalDateTime expiresAt;       // Retention expiry
    private List<ExportError> errors;      // Errors encountered
}

public enum ExportStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, EXPIRED
}
```

### ExportValidator

```java
public interface ExportValidator {
    // Validate full dataset
    ValidationResult validate(List<?> data, String contractName);
    
    // Validate single record
    ValidationResult validateRecord(Map<String, Object> record, 
                                    ContractDefinition contract);
    
    // Validate single field
    ValidationResult validateField(Object value, ContractField field);
}

public class ValidationResult {
    private boolean valid;
    private List<ValidationError> errors;
    private List<ValidationWarning> warnings;
}

public class ValidationError {
    private String fieldName;
    private String errorCode;
    private String message;
    private int recordNumber;
    private Object attemptedValue;
}
```

### ExportTransformer

```java
public interface ExportTransformer {
    // Serialize data to bytes
    byte[] transform(List<?> data, ExportFormat format, 
                    TransformOptions options);
    
    // Deserialize bytes to objects
    <T> List<T> deserialize(byte[] data, Class<T> type);
    
    // Apply masking for sensitive fields
    Map<String, Object> applyMasking(Map<String, Object> record,
                                     Application app,
                                     ContractDefinition contract);
}

public class TransformOptions {
    private boolean includeMetadata;
    private List<String> includedFields;  // null = all
    private List<String> excludedFields;
    private boolean compress;
    private String compressionFormat;     // gzip, brotli, etc.
}
```

## Packages

```
com.primus.export/
├── service/                    # Main service
│   └── ExportService
├── model/                      # DTOs and models
│   ├── ExportRequest
│   ├── Export
│   ├── ExportStatus
│   ├── ExportFormat
│   └── ExportError
├── pipeline/                   # Export pipeline components
│   ├── QueryResolver
│   ├── DataRetriever
│   ├── ValidationPipeline
│   ├── TransformationPipeline
│   └── StoragePipeline
├── validator/                  # Validators
│   ├── ExportValidator
│   ├── ContractValidator
│   └── FieldValidator
├── transformer/                # Transformers
│   ├── JsonTransformer
│   ├── CsvTransformer
│   ├── ParquetTransformer
│   └── MaskingTransformer
└── storage/                    # Storage backends
    ├── StorageBackend
    ├── NasStorageBackend
    ├── S3StorageBackend
    └── DbStorageBackend
```

## Dependencies

- **primus-common** - DTOs, exceptions
- **primus-core** - Contract definitions, transformers
- **primus-security** - Authorization checks
- External:
  - Jackson (JSON)
  - Apache Commons CSV
  - Apache Parquet
  - Spring Framework

## Configuration

```yaml
export:
  # Batch processing
  batchSize: 1000
  maxBatches: 10000
  
  # Async processing
  asyncEnabled: true
  threadPoolSize: 10
  maxConcurrentExports: 20
  
  # Validation
  validateBeforeStore: true
  validateSampling: false      # Sample validation for large exports
  samplingSize: 1000
  
  # Serialization
  compressOutput: true
  compressionFormat: gzip
  compressionLevel: 6          # 1-9
  
  # Retry
  maxRetries: 3
  retryDelayMs: 1000
  retryBackoffMultiplier: 2.0
  
  # Storage
  defaultBackend: s3
  backends:
    nas:
      enabled: true
    s3:
      enabled: true
    postgres:
      enabled: false
```

## Sequence

### Export Processing Flow

```
ExportRequest
    │
    ▼
┌─────────────────────────┐
│ QueryResolver           │
│ - Parse request         │
│ - Find contract         │
│ - Validate version      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ AuthorizationCheck      │
│ - Check permissions     │
│ - Verify app access     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ DataRetriever           │
│ - Query source systems  │
│ - Apply filters         │
│ - Batch/page data       │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ ValidationPipeline      │
│ - Type checking         │
│ - Pattern matching      │
│ - Enum validation       │
│ - Custom rules          │
└────────┬────────────────┘
         │
         ├─ Validation Failed?
         │  └─→ Collect errors
         │
         ▼
┌─────────────────────────┐
│ TransformationPipeline  │
│ - Apply formatters      │
│ - Resolve relationships │
│ - Serialize format      │
│ - Compress              │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ StoragePipeline         │
│ - Select backend        │
│ - Calculate checksum    │
│ - Store metadata        │
│ - Set expiration        │
└────────┬────────────────┘
         │
         ▼
ExportComplete (with ID + metadata)
```

## Extension Points

### Adding Custom Validator

```java
// Located in: com.primus.export.validator

public class CustomFieldValidator implements FieldValidator {
    
    @Override
    public ValidationResult validate(Object value, ContractField field) {
        if ("SPECIAL_TYPE".equals(field.getType())) {
            if (!isValidSpecialType(value)) {
                return ValidationResult.error(
                    "Invalid SPECIAL_TYPE format"
                );
            }
        }
        return ValidationResult.valid();
    }
}
```

### Adding Custom Transformer

```java
// Located in: com.primus.export.transformer

public class CustomFormatTransformer implements ExportTransformer {
    
    @Override
    public byte[] transform(List<?> data, ExportFormat format,
                           TransformOptions options) {
        if (format == ExportFormat.CUSTOM) {
            return transformToCustomFormat(data);
        }
        return null;
    }
}
```

### Adding Storage Backend

```java
// Located in: com.primus.export.storage

public class CustomStorageBackend implements StorageBackend {
    
    @Override
    public String store(byte[] data, ExportMetadata metadata) {
        // Custom storage logic
        return storageId;
    }
    
    @Override
    public byte[] retrieve(String storageId) {
        // Custom retrieval logic
        return data;
    }
}
```

## Future Enhancements

- [ ] Incremental exports (delta updates)
- [ ] Real-time streaming exports
- [ ] Partitioned storage for large datasets
- [ ] Deduplication across exports
- [ ] Smart compression based on data type
- [ ] Export scheduling/recurring exports
- [ ] Dependency tracking (export A depends on export B)
- [ ] Multi-format export in single request
- [ ] Export rollback capability
- [ ] Change data capture (CDC) integration

## Usage Example

```java
@Autowired
private ExportService exportService;

// Create export request
ExportRequest request = ExportRequest.builder()
    .contractName("UserProfile")
    .contractVersion("1.0")
    .format(ExportFormat.JSON)
    .destination("s3")
    .compress(true)
    .async(true)
    .filters(Map.of(
        "status", "ACTIVE",
        "createdAfter", "2024-01-01"
    ))
    .build();

// Initiate export
Export export = exportService.initiateExport(request);
String exportId = export.getExportId();

// Poll for completion
while (!export.getStatus().equals(ExportStatus.COMPLETED)) {
    Thread.sleep(1000);
    export = exportService.getExport(exportId);
}

// Success!
System.out.println("Exported " + export.getRecordCount() + " records");
```

## Testing

```bash
./gradlew :primus-platform:primus-annotations:primus-export:test
```

Tests cover:
- Request validation
- Contract resolution
- Data retrieval
- Validation pipeline
- Transformation pipeline
- Storage operations
- Error scenarios

## Build & Publish

```bash
./gradlew :primus-platform:primus-annotations:primus-export:build
```

---

**Related:**
- [06-export-flow.md](../06-export-flow.md) - Complete export workflow
- [09-yaml-reference.md](../09-yaml-reference.md) - YAML contract schema
- [primus-retrieval.md](./primus-retrieval.md) - Inverse operation

