# primus-annotations Module

## Purpose

Framework for defining declarative rules and metadata through Java annotations. These annotations drive contract validation, API generation, UI generation, and security policies—without requiring code changes at runtime.

## Responsibilities

✓ **Export Annotations**
- Mark classes/fields for export
- Specify sensitive data handling
- Define export filters
- Control field masking

✓ **API Contract Annotations**
- Define REST endpoints
- Describe request/response schemas
- Specify validation rules
- Document API behavior

✓ **Authentication Annotations**
- Mark protected endpoints
- Specify required roles
- Define permissions
- Control field access per role

✓ **Transformation Annotations**
- Custom formatters (date, currency)
- Field transformations
- Serialization rules
- Deserialization rules

## Public APIs

### Core Annotations

#### @ExportMarker
Marks a class for export processing.

```java
@ExportMarker
public class UserData {
    @ExportField
    private String userId;
    
    @ExportField(sensitive = true)
    private String personalIdNumber;
}
```

#### @ExportField
Marks a field for inclusion in export.

```java
@ExportField(
    name = "user_id",                    // Export name
    sensitive = false,                   // Requires masking?
    filterable = true,                   // Can filter on this?
    sortable = true,                     // Can sort by this?
    format = "UUID"                      // Custom format
)
private String userId;
```

#### @ApiContract
Defines a REST contract.

```java
@ApiContract(
    name = "UserProfile",
    version = "1.0",
    namespace = "com.primus.user",
    description = "User profile data"
)
public class UserProfile {
    @ApiField(required = true)
    private String userId;
}
```

#### @ApiField
Describes a contract field.

```java
@ApiField(
    required = true,
    description = "Unique identifier",
    example = "user_12345",
    pattern = "^user_\\d+$",
    minLength = 5,
    maxLength = 20,
    readOnly = false,
    writeOnly = false
)
private String userId;
```

#### @Authentication
Requires authentication for endpoint.

```java
@PostMapping("/exports")
@Authentication(required = true, roles = {"EDITOR"})
public ResponseEntity<Export> createExport(@RequestBody ExportRequest req) {
    // Implementation
}
```

#### @Authorization
Controls role-based access.

```java
@DeleteMapping("/exports/{id}")
@Authorization(
    roles = {"ADMIN"},
    permissions = {"DELETE_EXPORT"},
    fieldMasking = true
)
public ResponseEntity<Void> deleteExport(@PathVariable String id) {
    // Implementation
}
```

#### @Validator
Custom validation rule.

```java
@Validator(
    rule = "dateOfBirth < now()",
    message = "DOB must be in past"
)
public LocalDate dateOfBirth;
```

#### @Transformer
Custom data transformation.

```java
@ExportField
@Transformer(
    format = "PHONE",
    transformer = PhoneNumberFormatter.class,
    bidirectional = true
)
private String phone;
```

## Packages

```
com.primus.annotations/
├── export/                 # Export annotations
│   ├── @ExportMarker
│   ├── @ExportField
│   ├── @SensitiveData
│   └── @ExportFilter
├── api/                    # API annotations
│   ├── @ApiContract
│   ├── @ApiField
│   ├── @ApiEndpoint
│   └── @ApiExample
├── security/               # Security annotations
│   ├── @Authentication
│   ├── @Authorization
│   ├── @RoleRequired
│   └── @PermissionRequired
├── transform/              # Transformation annotations
│   ├── @Validator
│   ├── @Transformer
│   ├── @Format
│   └── @Deserialize
└── meta/                   # Meta-annotations
    ├── @ContractComponent
    └── @SecurityPolicy
```

## Dependencies

- **primus-common** - For exceptions, constants
- External:
  - Java 17+ (annotations built-in)
  - Lombok (optional, for generated code)

## Configuration

No runtime configuration—annotations are processed at:
- **Compile time**: Code generation (optional)
- **Startup time**: Spring container initialization
- **Runtime**: Reflection during request processing

To enable annotation processing:

```gradle
// build.gradle
dependencies {
    annotationProcessor 'com.primus:annotation-processor:1.0'
}
```

## Sequence

**primus-annotations** is purely declarative—it's used by other modules to understand developer intent.

```
Source Code with Annotations
    │
    ├─→ Annotation Processor (optional)
    │   └─→ Generate code
    │
    └─→ ClassLoader at Runtime
        │
        ├─→ Export Engine
        │   └─→ Reflect on @ExportField
        │
        ├─→ API Handler
        │   └─→ Reflect on @ApiContract
        │
        ├─→ Auth Filter
        │   └─→ Reflect on @Authentication
        │
        └─→ Transformer
            └─→ Reflect on @Transformer
```

## Extension Points

### Creating Custom Annotations

```java
// Step 1: Create the annotation
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface MyCustomAnno {
    String value() default "";
    boolean required() default false;
}

// Step 2: Use it
public class MyClass {
    @MyCustomAnno("my-value")
    private String myField;
}

// Step 3: Process it at runtime
for (Field field : clazz.getDeclaredFields()) {
    if (field.isAnnotationPresent(MyCustomAnno.class)) {
        MyCustomAnno anno = field.getAnnotation(MyCustomAnno.class);
        // Handle based on annotation
    }
}
```

### Composing Annotations

```java
// Create composite annotation
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@ApiContract
@ExportMarker
@Authentication(required = true)
public @interface SecureContract {
    String name();
    String version() default "1.0";
}

// Use composed annotation
@SecureContract(name = "UserProfile")
public class UserProfile {
    // Automatically gets all behaviors of ApiContract, 
    // ExportMarker, and @Authentication
}
```

## Future Enhancements

- [ ] Meta-annotation processor for custom rule generation
- [ ] Conditional annotations based on environment
- [ ] Annotation inheritance and hierarchy
- [ ] Repeatable annotations for multiple rules
- [ ] Integration with Spring's @Conditional
- [ ] Custom annotation validators
- [ ] Annotation documentation generator
- [ ] Dynamic annotation loading from external config

## Usage Examples

### Complete Example

```java
@ApiContract(name = "User", version = "1.0")
@ExportMarker
public class User {
    
    @ApiField(required = true)
    @ExportField
    private String userId;
    
    @ApiField(required = true)
    @ExportField
    @Transformer(format = "EMAIL")
    private String email;
    
    @ExportField(sensitive = true)
    @Validator(rule = "ssn matches pattern", pattern = "^\\d{3}-\\d{2}-\\d{4}$")
    private String ssn;
    
    @ApiField
    @ExportField
    @Transformer(format = "DATE", value = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    @ExportField(filterable = true, sortable = true)
    private UserStatus status;
}

// API Endpoint using annotations
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping
    @Authentication(required = true)
    @Authorization(roles = {"VIEWER"})
    public Page<User> list() {
        // Implementation
    }
    
    @PostMapping
    @Authentication(required = true, roles = {"EDITOR"})
    public ResponseEntity<User> create(@RequestBody User user) {
        // Implementation
    }
    
    @DeleteMapping("/{id}")
    @Authorization(roles = {"ADMIN"}, permissions = {"DELETE_USER"})
    public ResponseEntity<Void> delete(@PathVariable String id) {
        // Implementation
    }
}
```

## Testing

```bash
./gradlew :primus-platform:primus-annotations:test
```

Tests verify:
- Annotation retention (RUNTIME)
- Reflection-based discovery
- Composition behavior
- Inheritance handling

## Build & Publish

```bash
./gradlew :primus-platform:primus-annotations:build
./gradlew :primus-platform:primus-annotations:publishToMavenLocal
```

---

**Related:**
- [primus-export.md](./primus-export.md) - Uses @ExportField
- [primus-api.md](./primus-api.md) - Uses @ApiContract
- [primus-authenticator.md](./primus-authenticator.md) - Uses @Authentication

