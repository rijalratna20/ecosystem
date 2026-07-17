# Data Contracts

## Overview

Data contracts in Primus define the structure, validation rules, and metadata for API requests/responses and data entities.

## Contract Definition (YAML)

### Basic Structure

```yaml
name: UserProfile
version: "1.0"
description: User profile contract
namespace: com.primus.user

fields:
  - name: userId
    type: STRING
    required: true
    description: Unique user identifier
    
  - name: email
    type: EMAIL
    required: true
    
  - name: createdDate
    type: TIMESTAMP
    required: false
    
  - name: metadata
    type: OBJECT
    required: false
    nested:
      - name: tier
        type: STRING
        enum: [FREE, PREMIUM, ENTERPRISE]
```

## Contract Annotations

### Export Annotation

```java
@ExportField
public String userId;

@ExportField(format = "yyyy-MM-dd")
public LocalDate createdDate;
```

### API Annotation

```java
@ApiContract(name = "UserProfile", version = "1.0")
public class UserProfile {
    @ApiField(required = true)
    private String userId;
    
    @ApiField(pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
}
```

## Validation Rules

- **Type Validation**: Ensures data matches specified type
- **Required Validation**: Checks mandatory fields
- **Pattern Validation**: Regex patterns for strings
- **Enum Validation**: Restricts to defined values
- **Range Validation**: Numeric ranges
- **Custom Validators**: User-defined validation logic

## Contract Versioning

- Contracts follow semantic versioning
- Breaking changes increment major version
- Non-breaking changes increment minor version
- Maintenance patches increment patch version

