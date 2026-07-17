# YAML Contract Reference

## Complete Contract Schema

### Root Contract Definition

```yaml
name: ContractName                  # Required: Contract identifier
version: "1.0"                      # Required: Semantic version
description: "Human readable..."   # Optional: Description
namespace: "com.primus.domain"     # Optional: Java package namespace

metadata:                           # Optional: Contract metadata
  author: "team@company.com"
  tags: [tag1, tag2]
  deprecated: false

fields:                             # Required: Field definitions
  - name: fieldName
    type: STRING
    ...

indices:                            # Optional: Index definitions
  - name: email_index
    fields: [email]
    unique: true

validations:                        # Optional: Cross-field validations
  - name: date_range
    rule: "createdDate < modifiedDate"
```

## Field Definition

### Basic Field

```yaml
- name: fieldName                   # Required
  type: STRING                      # Required: Field type
  required: true                    # Optional: default false
  description: "Field description"  # Optional
  defaultValue: "default"           # Optional
```

### Field Types

```yaml
# Primitive Types
type: STRING                        # Java String
type: INTEGER                       # Java int/Integer
type: LONG                          # Java long/Long
type: DOUBLE                        # Java double/Double
type: BOOLEAN                       # Java boolean/Boolean
type: BYTE                          # Java byte/Byte
type: DATE                          # java.time.LocalDate
type: TIMESTAMP                     # java.time.LocalDateTime
type: TIME                          # java.time.LocalTime
type: UUID                          # java.util.UUID

# Special Types
type: EMAIL                         # Email string with validation
type: URL                           # URL string with validation
type: PHONE                         # Phone number string
type: JSON                          # JSON object/array
type: OBJECT                        # Nested object
type: ENUM                          # Enumeration
type: ARRAY                         # Array of items
```

### Advanced Field Options

```yaml
- name: email
  type: EMAIL
  required: true
  pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
  minLength: 5
  maxLength: 255

- name: age
  type: INTEGER
  minimum: 0
  maximum: 150

- name: salary
  type: DOUBLE
  minimum: 0
  format: currency
  precision: 2

- name: status
  type: ENUM
  enum: [ACTIVE, INACTIVE, PENDING]
  defaultValue: PENDING

- name: tags
  type: ARRAY
  items:
    type: STRING
  minItems: 1
  maxItems: 10

- name: metadata
  type: OBJECT
  nested:
    - name: tier
      type: STRING
      enum: [FREE, PREMIUM, ENTERPRISE]
    - name: features
      type: ARRAY
      items:
        type: STRING
```

### Format Specifiers

```yaml
- name: createdDate
  type: DATE
  format: "yyyy-MM-dd"

- name: timestamp
  type: TIMESTAMP
  format: "yyyy-MM-dd'T'HH:mm:ss'Z'"

- name: price
  type: DOUBLE
  format: currency
  precision: 2
```

## Validation Rules

### Field-Level Validation

```yaml
- name: password
  type: STRING
  required: true
  minLength: 8
  maxLength: 128
  pattern: "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"  # At least 1 upper, 1 digit
  description: "Must contain uppercase and digit"

- name: port
  type: INTEGER
  minimum: 1
  maximum: 65535

- name: percentage
  type: DOUBLE
  minimum: 0
  maximum: 100
  exclusiveMinimum: false
  exclusiveMaximum: false
```

### Nested Object Definition

```yaml
- name: address
  type: OBJECT
  required: true
  nested:
    - name: street
      type: STRING
      required: true
    - name: city
      type: STRING
      required: true
    - name: zipCode
      type: STRING
      pattern: "^[0-9]{5}(?:-[0-9]{4})?$"
    - name: country
      type: STRING
      enum: [US, CA, MX, ...]
```

### Array Definition

```yaml
- name: phoneNumbers
  type: ARRAY
  items:
    type: STRING
    pattern: "^\\d{10}$"
  minItems: 1
  maxItems: 5

- name: addresses
  type: ARRAY
  items:
    type: OBJECT
    nested:
      - name: street
        type: STRING
```

## Indices

```yaml
indices:
  - name: email_idx
    fields: [email]
    unique: true
  
  - name: user_status_idx
    fields: [status, createdDate]
    unique: false
```

## Contract Example

```yaml
name: UserProfile
version: "1.0"
description: "User profile contract"
namespace: "com.primus.user"

metadata:
  author: "platform-team@company.com"
  tags: [user, profile, core]

fields:
  - name: userId
    type: UUID
    required: true
    description: "Unique user identifier"
  
  - name: email
    type: EMAIL
    required: true
    description: "User email address"
  
  - name: fullName
    type: STRING
    required: true
    minLength: 1
    maxLength: 255
  
  - name: dateOfBirth
    type: DATE
    required: false
    format: "yyyy-MM-dd"
  
  - name: status
    type: ENUM
    enum: [ACTIVE, INACTIVE, SUSPENDED]
    defaultValue: ACTIVE
    required: true
  
  - name: tier
    type: ENUM
    enum: [FREE, PREMIUM, ENTERPRISE]
    required: true
  
  - name: address
    type: OBJECT
    nested:
      - name: street
        type: STRING
        required: true
      - name: city
        type: STRING
        required: true
      - name: state
        type: STRING
        required: false
      - name: zipCode
        type: STRING
        required: true
      - name: country
        type: STRING
        required: true
  
  - name: phoneNumbers
    type: ARRAY
    items:
      type: STRING
      pattern: "^\\d{10}$"
    maxItems: 3
  
  - name: createdDate
    type: TIMESTAMP
    required: true
    format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
  
  - name: updatedDate
    type: TIMESTAMP
    required: false

indices:
  - name: email_idx
    fields: [email]
    unique: true
  
  - name: status_idx
    fields: [status, createdDate]
```

