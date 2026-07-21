# primus-plugin Module

## Purpose

Lightweight plugin framework that makes the Primus platform extensible without modifying core code. Defines the stable SPI (Service Provider Interface) contracts that third-party developers and internal teams implement to add custom masking providers, transformers, validators, storage connectors, and event listeners that are loaded into `primus-server` at runtime.

## Module Location

```
primus-platform/primus-plugin/
```

## Responsibilities

✓ **Plugin SPI Definition**
- Define `Plugin` lifecycle interface (`initialize` / `shutdown`)
- Define `PluginContext` for accessing platform services and configuration
- Provide stable versioned contracts that plugin authors can depend on

✓ **Plugin Loading**
- Discover plugins on the classpath (SPI via `ServiceLoader`)
- Support runtime installation of plugin JARs (future: admin API)
- Manage plugin load order and dependency resolution

✓ **Extension Point Contracts**
- `TransformerPlugin` – custom field transformers
- `ValidatorPlugin` – custom contract validators
- `StoragePlugin` – custom storage backends
- `EventListenerPlugin` – react to platform lifecycle events
- `MaskingPlugin` – custom field masking strategies

✓ **Plugin Isolation**
- Each plugin receives its own configuration namespace
- Plugins cannot access each other's state
- Plugin errors are isolated (one plugin crash does not take down the platform)

## SPI Interfaces

### Plugin (Core Lifecycle)

```java
package com.primus.plugin.spi;

/**
 * Marker interface for Primus plugins. Implementations should provide a no-arg
 * constructor and register via the platform's plugin loader (classpath or
 * runtime installer).
 */
public interface Plugin {
    /**
     * Called once when the plugin is initialized by the platform.
     */
    void initialize(PluginContext context);

    /**
     * Called on platform shutdown or plugin unload.
     */
    void shutdown();
}
```

### PluginContext

```java
package com.primus.plugin.spi;

import java.util.Map;

/**
 * Minimal context provided to plugins on initialization. Can be extended with
 * configuration, logger, service locators, etc.
 */
public interface PluginContext {
    /** Plugin-specific configuration from primus-server's application.yaml. */
    Map<String, String> getConfig();
}
```

### TransformerPlugin

```java
package com.primus.plugin.spi;

import java.util.Map;

/**
 * Plugin that adds a named field transformer to the transformation pipeline.
 * Register with @ExportField(transformer = "my-transformer") in contracts.
 */
public interface TransformerPlugin extends Plugin {
    /** Unique transformer name referenced in YAML contracts. */
    String transformerName();

    /** Transform a single field value. */
    Object transform(String fieldName, Object value, Map<String, String> options);
}
```

### ValidatorPlugin

```java
package com.primus.plugin.spi;

import java.util.List;
import java.util.Map;

/**
 * Plugin that adds custom validation rules to the contract validation engine.
 */
public interface ValidatorPlugin extends Plugin {
    /** Name of this validator, referenced in contract YAML. */
    String validatorName();

    /** Validate the given record. Return empty list if valid. */
    List<String> validate(Map<String, Object> record, Map<String, String> options);
}
```

### StoragePlugin

```java
package com.primus.plugin.spi;

import java.util.List;
import java.util.Map;

/**
 * Plugin that adds a new storage backend to the storage routing engine.
 */
public interface StoragePlugin extends Plugin {
    /** Unique backend identifier (used in contract storagePolicy.backend). */
    String backendId();

    /** Write records to this storage backend. Return a backend-specific reference. */
    String write(String exportId, List<Map<String, Object>> records);

    /** Read records from this storage backend. */
    List<Map<String, Object>> read(String storageRef);

    /** Delete records from this storage backend. */
    void delete(String storageRef);

    /** Return true if this backend is healthy and available. */
    boolean isAvailable();
}
```

### EventListenerPlugin

```java
package com.primus.plugin.spi;

/**
 * Plugin that receives platform lifecycle events.
 */
public interface EventListenerPlugin extends Plugin {
    /** Called when an export completes successfully. */
    default void onExportCompleted(String exportId, String contractName, long recordCount) {}

    /** Called when an export fails. */
    default void onExportFailed(String exportId, String contractName, String reason) {}

    /** Called when a new contract is registered. */
    default void onContractRegistered(String contractName, String version) {}
}
```

### MaskingPlugin

```java
package com.primus.plugin.spi;

/**
 * Plugin that adds a named masking strategy for sensitive fields.
 * Reference with @ExportField(sensitive = true, maskingStrategy = "my-masker").
 */
public interface MaskingPlugin extends Plugin {
    /** Masking strategy name referenced in contracts. */
    String maskingStrategyName();

    /** Apply masking to the field value. */
    String mask(String fieldName, String value);
}
```

## Packages

```
com.primus.plugin
├── spi/                   # Stable plugin contracts (versioned)
│   ├── Plugin             # Core lifecycle
│   ├── PluginContext      # Context provided to plugins
│   ├── TransformerPlugin  # Custom transformers
│   ├── ValidatorPlugin    # Custom validators
│   ├── StoragePlugin      # Custom storage backends
│   ├── EventListenerPlugin# Event listeners
│   └── MaskingPlugin      # Custom masking strategies
└── (loader and registry live in primus-server)
```

## Dependencies

- **None** – this module is intentionally dependency-free so plugin authors can depend on it without pulling in the full Primus stack.
- External: Java 17 stdlib only.

## Configuration

Plugin-specific configuration is namespaced under `primus.plugins.<pluginId>` in `primus-server`'s `application.yaml`:

```yaml
primus:
  plugins:
    my-custom-masker:
      saltKey: ${MY_MASKER_SALT}
      redactBelow: 4

    kafka-storage:
      bootstrapServers: kafka:9092
      topic: primus-exports
      compressionType: lz4
```

The configuration is passed to the plugin at initialization via `PluginContext.getConfig()`.

## Writing a Plugin

### Step 1 – Add the dependency

```gradle
// plugin-author's build.gradle
dependencies {
    implementation project(':primus-platform:primus-plugin')
    // OR, when published to Maven:
    // implementation 'com.primus:primus-plugin:1.0.0'
}
```

### Step 2 – Implement the SPI

```java
// Custom SHA-256 masking plugin
public class Sha256MaskingPlugin implements MaskingPlugin {

    private String salt;

    @Override
    public void initialize(PluginContext context) {
        this.salt = context.getConfig().getOrDefault("salt", "");
    }

    @Override
    public void shutdown() { }

    @Override
    public String maskingStrategyName() { return "sha256"; }

    @Override
    public String mask(String fieldName, String value) {
        return HashUtils.sha256(value + salt);
    }
}
```

### Step 3 – Register via ServiceLoader

```
# src/main/resources/META-INF/services/com.primus.plugin.spi.MaskingPlugin
com.example.myplugin.Sha256MaskingPlugin
```

### Step 4 – Package and install

```bash
# Build your plugin JAR
./gradlew jar

# Add it to primus-server's classpath (or drop into plugins/ directory)
cp build/libs/my-plugin-1.0.0.jar $PRIMUS_HOME/plugins/
```

### Step 5 – Reference in contract YAML

```yaml
fields:
  - name: ssn
    type: STRING
    sensitive: true
    maskingStrategy: sha256   # ← your plugin's maskingStrategyName()
```

## Built-in Plugin Examples

### Custom Transformer Plugin

```java
// Converts ISO-3166 country codes to full names
public class CountryNameTransformer implements TransformerPlugin {

    @Override
    public void initialize(PluginContext ctx) { }

    @Override
    public void shutdown() { }

    @Override
    public String transformerName() { return "country-name"; }

    @Override
    public Object transform(String field, Object value, Map<String, String> opts) {
        return CountryUtils.toFullName((String) value);
    }
}
```

### Custom Validator Plugin

```java
// Validates that IBAN fields are syntactically correct
public class IbanValidatorPlugin implements ValidatorPlugin {

    @Override
    public void initialize(PluginContext ctx) { }

    @Override
    public void shutdown() { }

    @Override
    public String validatorName() { return "iban"; }

    @Override
    public List<String> validate(Map<String, Object> record, Map<String, String> opts) {
        String iban = (String) record.get("iban");
        if (!IbanUtils.isValid(iban)) {
            return List.of("Field 'iban' contains an invalid IBAN: " + iban);
        }
        return List.of();
    }
}
```

## Plugin Lifecycle

```
primus-server starts
    │
    ▼
PluginLoader.discover()
    │ Scans classpath for META-INF/services/com.primus.plugin.spi.*
    │ and plugins/ directory (if configured)
    │
    ▼
For each discovered plugin:
    Plugin.initialize(PluginContext)
    │
    ├─ TransformerPlugins → registered in TransformPipeline
    ├─ StoragePlugins     → registered in StorageRouter
    ├─ ValidatorPlugins   → registered in ContractValidator
    ├─ MaskingPlugins     → registered in MaskRegistry
    └─ EventListeners     → registered in EventBus
    │
    ▼
Platform ready (plugins active)
    │
    ▼
Shutdown signal
    │
    ▼
For each plugin:
    Plugin.shutdown()
```

## Future Enhancements

- [ ] Admin REST API to install / uninstall plugins without restart
- [ ] Plugin versioning and compatibility matrix
- [ ] Plugin signing (verify publisher identity)
- [ ] Plugin marketplace UI
- [ ] Hot-reload without platform restart
- [ ] Dependency injection support in `PluginContext` (access platform beans)
- [ ] Sandboxed classloader for stronger plugin isolation

## Testing

```bash
./gradlew :primus-platform:primus-plugin:test
```

The smoke test (`PluginSmokeTest.java`) verifies the SPI interfaces compile and basic lifecycle works.

For plugin authors, a test harness is provided:

```java
// Test your plugin in isolation
PluginContext ctx = TestPluginContext.of(Map.of("salt", "test-salt"));
Sha256MaskingPlugin plugin = new Sha256MaskingPlugin();
plugin.initialize(ctx);

String masked = plugin.mask("ssn", "123-45-6789");
assertThat(masked).hasSize(64);  // SHA-256 hex length
```

## Build

```bash
./gradlew :primus-platform:primus-plugin:build
```

---

**Related:**
- [primus-core.md](../docs/modules/primus-core.md) – storage provider SPI consumer
- [primus-annotations.md](../docs/modules/primus-annotations.md) – annotation-driven extension points
- [primus-server.md](../docs/modules/primus-server.md) – loads and manages plugins at runtime
