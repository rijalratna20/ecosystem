# yaml-generator Module

## Overview

Utility module for generating YAML contract definitions from various sources and formats.

## Module Location

```
primus-platform/primus-libs/primus-yaml-generator/ (Conceptual)
```

## Purpose

Automates contract generation from:
- Existing database schemas
- OpenAPI/Swagger definitions
- Java POJOs with annotations
- Excel/CSV specifications
- JSON Schema definitions

## Core Components

### ContractGenerator

```java
public interface ContractGenerator {
    ContractDefinition generate(Object source);
    List<ContractDefinition> generateBatch(Collection<?> sources);
}
```

### Generation Strategies

#### 1. Database Schema Generator

```java
@Service
public class DatabaseSchemaGenerator implements ContractGenerator {
    
    public ContractDefinition generate(DatabaseTable table) {
        ContractDefinition contract = new ContractDefinition();
        contract.setName(toContractName(table.getName()));
        contract.setVersion("1.0");
        
        for (Column column : table.getColumns()) {
            ContractField field = mapColumnToField(column);
            contract.addField(field);
        }
        
        return contract;
    }
    
    private ContractField mapColumnToField(Column column) {
        ContractField field = new ContractField();
        field.setName(column.getName());
        field.setType(mapSqlType(column.getType()));
        field.setRequired(!column.isNullable());
        return field;
    }
}
```

#### 2. OpenAPI Generator

```java
@Service
public class OpenApiSchemaGenerator implements ContractGenerator {
    
    public List<ContractDefinition> generate(OpenAPI openApi) {
        List<ContractDefinition> contracts = new ArrayList<>();
        
        for (Schema<?> schema : openApi.getComponents().getSchemas().values()) {
            ContractDefinition contract = new ContractDefinition();
            contract.setName(schema.getTitle());
            contract.setDescription(schema.getDescription());
            
            for (String propName : schema.getProperties().keySet()) {
                Schema<?> propSchema = schema.getProperties().get(propName);
                ContractField field = mapSchemaToField(propName, propSchema);
                contract.addField(field);
            }
            
            contracts.add(contract);
        }
        
        return contracts;
    }
}
```

#### 3. Java POJO Generator

```java
@Service
public class PojoSchemaGenerator implements ContractGenerator {
    
    public ContractDefinition generate(Class<?> clazz) {
        ContractDefinition contract = new ContractDefinition();
        contract.setName(clazz.getSimpleName());
        contract.setNamespace(clazz.getPackageName());
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExportField.class)) {
                ContractField contractField = mapFieldToContractField(field);
                contract.addField(contractField);
            }
        }
        
        return contract;
    }
    
    private ContractField mapFieldToContractField(Field field) {
        ExportField annotation = field.getAnnotation(ExportField.class);
        ContractField contractField = new ContractField();
        contractField.setName(field.getName());
        contractField.setType(mapJavaType(field.getType()));
        contractField.setRequired(annotation.required());
        return contractField;
    }
}
```

#### 4. JSON Schema Generator

```java
@Service
public class JsonSchemaGenerator implements ContractGenerator {
    
    public ContractDefinition generate(JsonNode jsonSchema) {
        ContractDefinition contract = new ContractDefinition();
        contract.setName(jsonSchema.get("title").asText());
        contract.setDescription(jsonSchema.get("description").asText());
        
        JsonNode properties = jsonSchema.get("properties");
        for (Iterator<String> it = properties.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            JsonNode fieldSchema = properties.get(fieldName);
            ContractField field = mapJsonNodeToField(fieldName, fieldSchema);
            contract.addField(field);
        }
        
        return contract;
    }
}
```

## Type Mapping

### SQL to Contract Types

```java
private FieldType mapSqlType(String sqlType) {
    return switch (sqlType.toUpperCase()) {
        case "VARCHAR" -> FieldType.STRING;
        case "INT", "INTEGER" -> FieldType.INTEGER;
        case "BIGINT" -> FieldType.LONG;
        case "DECIMAL", "NUMERIC" -> FieldType.DOUBLE;
        case "BOOLEAN" -> FieldType.BOOLEAN;
        case "DATE" -> FieldType.DATE;
        case "TIMESTAMP" -> FieldType.TIMESTAMP;
        case "TEXT", "CLOB" -> FieldType.STRING;
        case "BLOB" -> FieldType.STRING;
        default -> FieldType.STRING;
    };
}
```

### Java to Contract Types

```java
private FieldType mapJavaType(Class<?> javaType) {
    if (javaType == String.class) return FieldType.STRING;
    if (javaType == Integer.class || javaType == int.class) return FieldType.INTEGER;
    if (javaType == Long.class || javaType == long.class) return FieldType.LONG;
    if (javaType == Double.class || javaType == double.class) return FieldType.DOUBLE;
    if (javaType == Boolean.class || javaType == boolean.class) return FieldType.BOOLEAN;
    if (javaType == LocalDate.class) return FieldType.DATE;
    if (javaType == LocalDateTime.class) return FieldType.TIMESTAMP;
    if (javaType == UUID.class) return FieldType.UUID;
    return FieldType.STRING;
}
```

## YAML Output

### Contract YAML Format

```yaml
name: UserProfile
version: "1.0"
namespace: "com.primus.user"
description: "User profile data contract"

fields:
  - name: userId
    type: UUID
    required: true
    description: "Unique user identifier"
  
  - name: email
    type: EMAIL
    required: true
    description: "User email address"
    pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
  
  - name: fullName
    type: STRING
    required: true
    minLength: 1
    maxLength: 255
  
  - name: status
    type: ENUM
    enum: [ACTIVE, INACTIVE, SUSPENDED]
    defaultValue: ACTIVE
    required: true

indices:
  - name: email_idx
    fields: [email]
    unique: true
  
  - name: status_idx
    fields: [status]
```

## Command-Line Interface

### CLI Usage

```bash
# Generate from database
java -jar yaml-generator.jar \
  --source=database \
  --db-url="jdbc:postgresql://localhost:5432/primus" \
  --db-user=primus \
  --schema=public \
  --output=/contracts/generated/

# Generate from OpenAPI
java -jar yaml-generator.jar \
  --source=openapi \
  --openapi-file=api.yaml \
  --output=/contracts/generated/

# Generate from Java classes
java -jar yaml-generator.jar \
  --source=java \
  --classpath=/app/classes \
  --package=com.company.models \
  --output=/contracts/generated/

# Generate from JSON Schema
java -jar yaml-generator.jar \
  --source=json-schema \
  --schema-file=schema.json \
  --output=/contracts/generated/
```

## Programmatic Usage

```java
// Database generation
DatabaseSchemaGenerator dbGen = new DatabaseSchemaGenerator();
ContractDefinition contract = dbGen.generate(userTable);

// Java POJO generation
PojoSchemaGenerator pojoGen = new PojoSchemaGenerator();
ContractDefinition contract = pojoGen.generate(UserProfile.class);

// OpenAPI generation
OpenApiSchemaGenerator openApiGen = new OpenApiSchemaGenerator();
List<ContractDefinition> contracts = openApiGen.generate(openApi);

// Write to file
YamlWriter writer = new YamlWriter();
writer.write(contract, "/tmp/UserProfile.yaml");
```

## Validation

### Generated Contract Validation

```java
@Service
public class GeneratedContractValidator {
    
    public ValidationResult validate(ContractDefinition contract) {
        ValidationResult result = new ValidationResult();
        
        // Validate required fields
        if (!contract.hasName()) {
            result.addError("Contract name is required");
        }
        
        // Validate field definitions
        for (ContractField field : contract.getFields()) {
            if (!isValidFieldDefinition(field)) {
                result.addError("Invalid field: " + field.getName());
            }
        }
        
        // Validate type mappings
        for (ContractField field : contract.getFields()) {
            if (!isSupportedType(field.getType())) {
                result.addError("Unsupported type for field: " + field.getName());
            }
        }
        
        return result;
    }
}
```

## Configuration

```yaml
yaml-generator:
  # Database connection
  database:
    driver: org.postgresql.Driver
    url: "jdbc:postgresql://localhost:5432/primus"
  
  # OpenAPI
  openapi:
    includeExtensions: true
    resolveReferences: true
  
  # Java
  java:
    scanAnnotations: [ExportField, ApiField]
    includeGettersSetters: false
  
  # Output
  output:
    format: yaml
    prettyPrint: true
    includeComments: true
```

## Gradle Integration

```gradle
task generateContractsFromDb {
    doLast {
        GenerateContractsTask.main([
            '--source': 'database',
            '--db-url': 'jdbc:postgresql://localhost:5432/primus',
            '--output': 'src/main/resources/contracts/'
        ] as String[])
    }
}

task generateContractsFromJava {
    doLast {
        GenerateContractsTask.main([
            '--source': 'java',
            '--classpath': 'build/classes/java/main',
            '--package': 'com.company.models',
            '--output': 'src/main/resources/contracts/'
        ] as String[])
    }
}
```

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-yaml-generator:test
```

## Use Cases

1. **Onboarding**: Auto-generate contracts from existing systems
2. **API Migration**: Convert OpenAPI to Primus contracts
3. **Database Schemas**: Expose database as contracts
4. **Bulk Generation**: Rapidly generate contracts for multiple entities

