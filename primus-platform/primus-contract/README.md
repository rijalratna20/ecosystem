# primus-contract

Metadata contract parsing and validation framework.

## Overview

`primus-contract` parses, validates, and manages application metadata contracts (YAML-based definitions of collections, relationships, search fields, and sensitive data).

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-contract')
}
```

### Basic Usage

```java
ContractParser parser = new ContractParser();
ApplicationContract contract = parser.parse(yamlFile);

// Access collections
List<Collection> collections = contract.getCollections();

// Validate structure
ValidationResult result = contract.validate();
if (result.hasErrors()) {
    result.getErrors().forEach(System.out::println);
}
```

## Key Concepts

### Application Contract
Defines a single application's data model:
```yaml
application:
  name: "CustomerService"
  description: "Customer data management"
collections:
  - name: "customers"
    entity: "Customer"
    fields: [...]
  - name: "accounts"
    entity: "Account"
    fields: [...]
relationships:
  - from: "customers"
    to: "accounts"
    type: "ONE_TO_MANY"
```

### Collection Definition
```yaml
- name: "customers"
  entity: "Customer"
  searchable: true
  fields:
    - name: "customerId"
      type: "STRING"
      sensitive: false
    - name: "ssn"
      type: "STRING"
      sensitive: true
      mask: "***-**-####"
```

## Configuration

Set parser options:
```java
ContractParserConfig config = ContractParserConfig.builder()
    .enableStrictMode(true)
    .allowCustomTypes(false)
    .maxCollectionsPerApp(100)
    .build();

ContractParser parser = new ContractParser(config);
```

## Testing

```bash
./gradlew :primus-platform:primus-contract:test
```

## Validation Rules

✅ Application name is required  
✅ At least one collection must be defined  
✅ Collection names are unique within app  
✅ Field names are unique within collection  
✅ Sensitive fields have masking rules defined  
✅ Relationships reference existing collections  

## Module Dependencies

- `primus-common`

## APIs

### ContractParser
- `parse(File yamlFile)` → ApplicationContract
- `parse(String yamlContent)` → ApplicationContract
- `validate(ApplicationContract)` → ValidationResult

### ApplicationContract
- `getApplicationName()` → String
- `getCollections()` → List<Collection>
- `getRelationships()` → List<Relationship>
- `getCollection(String name)` → Collection

## Troubleshooting

**Issue**: "Collection not found" validation error
- **Solution**: Check YAML syntax and ensure collection name matches references

**Issue**: "Invalid mask pattern"
- **Solution**: Use valid patterns: `***-**-####`, `[REDACTED]`, etc.

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

