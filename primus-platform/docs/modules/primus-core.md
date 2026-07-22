# primus-core Module

## Purpose

Heart of the Primus data-governance engine. Provides the contract-parsing engine, export transaction management, data-transformation pipelines, and the routing logic that ties together storage, validation, and retrieval. All higher-level services (primus-server, primus-export, primus-retrieval) orchestrate their core operations through this module.

## Module Location

```
primus-platform/primus-libs/primus-core/
```

## Responsibilities

✓ **Contract Parsing & Registry**
- Parse YAML metadata contracts into typed Java models
- Validate contract structure and semantic rules
- Version contracts and manage schema evolution
- Provide an in-memory registry with fast lookups

✓ **Export Transaction Management**
- Coordinate multi-step export transactions (begin → validate → transform → store → commit)
- Rollback on failure with compensating actions
- Idempotency (duplicate-export detection)
- Status tracking (PENDING → IN_PROGRESS → COMPLETED / FAILED)

✓ **Data Transformation Pipeline**
- Apply field-level transformers (masking, formatting, redaction)
- Support pluggable transformer chains
- Type coercion between source formats and target formats
- Transformation audit (before/after snapshots for compliance)

✓ **Storage Routing**
- Select the appropriate storage backend (NAS, S3, DB) based on contract policy
- Delegate read/write to the `StorageProvider` SPI
- Manage multi-backend fan-out (write to two backends simultaneously)

✓ **Event Publishing**
- Emit lifecycle events (ExportStarted, ExportCompleted, ExportFailed)
- Enable downstream modules (audit, notification) to react asynchronously

## Public APIs

### ContractRegistry

```java
public interface ContractRegistry {
    /** Load and register a contract from YAML source. */
    Contract register(String yamlContent) throws ContractValidationException;

    /** Get a specific version of a contract. */
    Optional<Contract> find(String name, String version);

    /** Get the latest version. */
    Optional<Contract> findLatest(String name);

    /** List all registered contracts. */
    List<ContractSummary> listAll();

    /** Deregister a contract. */
    void remove(String name, String version);
}
```

### Contract Model

```java
public class Contract {
    private String name;                    // Unique contract name
    private String version;                 // Semver (1.0.0)
    private String namespace;               // Java package namespace
    private String description;
    private List<ContractField> fields;
    private List<ExportPolicy> policies;
    private StoragePolicy storagePolicy;
    private Instant registeredAt;
    private String registeredBy;
}

public class ContractField {
    private String name;
    private FieldType type;                 // STRING, NUMBER, DATE, BOOLEAN
    private boolean required;
    private boolean sensitive;              // Requires masking / approval
    private boolean filterable;
    private boolean sortable;
    private String defaultTransformer;      // Transformer bean name
    private String description;
    private String example;
}

public enum FieldType { STRING, NUMBER, DATE, BOOLEAN, OBJECT, ARRAY }
```

### ExportTransaction

```java
public interface ExportTransactionManager {
    /** Begin a new export transaction. */
    ExportTransaction begin(ExportRequest request);

    /** Execute all pipeline stages (validate → transform → store). */
    ExportResult execute(ExportTransaction tx);

    /** Commit a successful transaction. */
    void commit(String transactionId);

    /** Roll back a failed transaction, removing partial data. */
    void rollback(String transactionId);

    /** Query status. */
    TransactionStatus getStatus(String transactionId);
}

public class ExportTransaction {
    private String transactionId;
    private String contractName;
    private String contractVersion;
    private TransactionStatus status;
    private ExportRequest request;
    private List<TransformationStep> pipeline;
    private StorageTarget storageTarget;
    private Instant startedAt;
    private Map<String, Object> context;
}

public enum TransactionStatus {
    PENDING, VALIDATING, TRANSFORMING, STORING, COMPLETED, FAILED, ROLLED_BACK
}
```

### Transformation Pipeline

```java
public interface Transformer {
    /** Transform a single record. */
    Map<String, Object> transform(Map<String, Object> record, TransformContext ctx);

    /** Transformer identifier (used in contract YAML). */
    String name();
}

public class TransformContext {
    private Contract contract;
    private PrimusUser user;
    private Map<String, Object> options;
}

// Built-in transformers
public class MaskTransformer        implements Transformer { }  // Masks sensitive fields
public class DateFormatTransformer  implements Transformer { }  // Formats dates
public class CurrencyTransformer    implements Transformer { }  // Formats monetary values
public class RedactTransformer      implements Transformer { }  // Fully removes a field
public class HashTransformer        implements Transformer { }  // SHA-256 hash of value
```

### Storage Provider SPI

```java
public interface StorageProvider {
    /** Provider identifier – matches contract storagePolicy.backend value. */
    String id();

    /** Write data to storage. Returns a storage reference. */
    StorageReference write(String exportId, List<Map<String, Object>> records, StorageOptions options);

    /** Read data from storage. */
    List<Map<String, Object>> read(StorageReference ref, ReadOptions options);

    /** Delete data from storage. */
    void delete(StorageReference ref);

    /** Health check. */
    boolean isAvailable();
}

public class StorageReference {
    private String backend;     // "nas", "s3", "database"
    private String path;        // Backend-specific path / key / table
    private long sizeBytes;
    private String checksum;
    private Instant storedAt;
}
```

### Event Publishing

```java
public interface CoreEventPublisher {
    void publish(ExportStartedEvent event);
    void publish(ExportCompletedEvent event);
    void publish(ExportFailedEvent event);
    void publish(ContractRegisteredEvent event);
    void publish(ContractRemovedEvent event);
}
```

## Packages

```
com.primus.core
├── contract/              # Contract model and registry
│   ├── Contract
│   ├── ContractField
│   ├── ContractRegistry
│   ├── ContractRegistryImpl
│   ├── ContractParser          # YAML → Contract
│   └── ContractValidator
├── transaction/           # Export transaction management
│   ├── ExportTransaction
│   ├── ExportTransactionManager
│   ├── ExportTransactionManagerImpl
│   └── TransactionStatus
├── transform/             # Transformation pipeline
│   ├── Transformer (interface)
│   ├── TransformPipeline
│   ├── TransformContext
│   ├── MaskTransformer
│   ├── DateFormatTransformer
│   ├── CurrencyTransformer
│   ├── RedactTransformer
│   └── HashTransformer
├── storage/               # Storage provider SPI + routing
│   ├── StorageProvider (interface)
│   ├── StorageRouter
│   ├── StorageReference
│   └── StorageOptions
└── event/                 # Core domain events
    ├── CoreEventPublisher
    ├── ExportStartedEvent
    ├── ExportCompletedEvent
    ├── ExportFailedEvent
    ├── ContractRegisteredEvent
    └── ContractRemovedEvent
```

## Dependencies

- **primus-common** – DTOs, exceptions, logging
- **primus-utils** – field masking, format conversion, validators
- **primus-annotations** – reads `@ExportMarker`, `@ExportField` annotations
- External:
  - SnakeYAML (contract parsing)
  - Spring Context (bean injection for pluggable transformers)
  - Jackson (JSON serialisation)

## Configuration

```yaml
primus:
  core:
    contract:
      registry:
        maxVersionsPerContract: 10
        scanPackages: com.primus.contracts     # Annotation-based registration

    transaction:
      timeoutSeconds: 300          # 5-minute export timeout
      idempotencyWindowMinutes: 60 # Duplicate detection window

    transform:
      defaultChain:
        - mask-sensitive
        - format-dates
        - format-currency

    storage:
      defaultBackend: nas
      fallbackBackend: s3          # Used if primary unavailable
      fanOut: false                # Write to both backends simultaneously
```

## Transaction Lifecycle

```
ExportRequest
    │
    ▼
ExportTransactionManager.begin()
    │
    ├─ Assign transactionId
    ├─ Lookup Contract from registry
    ├─ Validate request fields
    │
    ▼
TransformPipeline.run(records)
    │
    ├─ MaskTransformer
    ├─ DateFormatTransformer
    └─ (custom transformers)
    │
    ▼
StorageRouter.route(contract) → StorageProvider
    │
    ▼
StorageProvider.write(exportId, records)
    │
    ▼
CoreEventPublisher.publish(ExportCompletedEvent)
    │
    ▼
ExportTransactionManager.commit()
```

## Extension Points

### Registering a Custom Transformer

```java
@Component
public class JsonFlattenTransformer implements Transformer {

    @Override
    public String name() { return "json-flatten"; }

    @Override
    public Map<String, Object> transform(Map<String, Object> record, TransformContext ctx) {
        // Flatten nested objects into dot-notation keys
        return MapUtils.flatten(record);
    }
}
// Reference in contract YAML: transformer: json-flatten
```

### Registering a Custom Storage Provider

```java
@Component
public class PostgresStorageProvider implements StorageProvider {

    @Override
    public String id() { return "database"; }

    @Override
    public StorageReference write(String exportId, List<Map<String, Object>> records, StorageOptions opts) {
        // Insert records into PostgreSQL
    }

    @Override
    public List<Map<String, Object>> read(StorageReference ref, ReadOptions opts) {
        // Query records from PostgreSQL
    }
    // ...
}
```

## Future Enhancements

- [ ] Streaming transaction support (process records in batches, not all in memory)
- [ ] Distributed transaction coordination (XA / saga pattern)
- [ ] Contract schema diff and migration tooling
- [ ] Pluggable event bus (currently in-process, extract to Kafka/RabbitMQ)
- [ ] Contract versioning with backward-compatibility validation
- [ ] Query planner for multi-source joins

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-core:test
```

Key test scenarios:
- YAML contract parsing (valid / invalid YAML)
- Transaction commit and rollback
- Transformer chain ordering and idempotency
- Storage routing fallback
- Duplicate export detection

## Build & Publish

```bash
./gradlew :primus-platform:primus-libs:primus-core:build
./gradlew :primus-platform:primus-libs:primus-core:publishToMavenLocal
```

---

**Related:**
- [primus-export.md](./primus-export.md) – consumer of the transaction manager
- [primus-retrieval.md](./primus-retrieval.md) – consumer of storage router
- [primus-common.md](./primus-common.md) – shared exceptions and DTOs
- [03-contracts.md](../03-contracts.md) – contract YAML format
