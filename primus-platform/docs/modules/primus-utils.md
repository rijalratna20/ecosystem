# primus-utils Module

## Purpose

Supplementary utility library providing higher-level helpers that build on top of `primus-common`. Contains domain-specific formatting, validation, data-transformation, and miscellaneous convenience functions used across the Primus platform.

## Module Location

```
primus-platform/primus-libs/primus-utils/
```

## Responsibilities

✓ **Data Formatting**
- Currency / monetary value formatting
- Phone-number normalisation and formatting
- Masked field output (SSN, card numbers)
- Localisation helpers (locale-aware date/number/currency)

✓ **Validation Helpers**
- Field-level validators (email, phone, SSN, UUID, URL)
- Schema validators (YAML contract structure)
- Cross-field validators (date ranges, consistency checks)

✓ **Data Transformation**
- Format converters (CSV ↔ JSON ↔ XML)
- Field-type coercions (String → LocalDate, etc.)
- Struct flatteners / key-path navigators

✓ **Encoding & Hashing**
- Base64 encode/decode
- SHA-256 / MD5 hashing helpers
- URL-safe encoding

✓ **Retry & Resilience Helpers**
- Exponential backoff utility
- Retry policy builder
- Circuit breaker state helpers

## Public APIs

### Formatting

```java
// Monetary values
MoneyUtils.format(BigDecimal.valueOf(12345.67), "USD", Locale.US)
// → "$12,345.67"

MoneyUtils.format(BigDecimal.valueOf(12345.67), "EUR", Locale.GERMANY)
// → "12.345,67 €"

// Phone normalisation
PhoneUtils.normalize("+1 (800) 555-0100")   // → "+18005550100"
PhoneUtils.isValid("+18005550100")           // → true

// Masking
MaskUtils.maskEmail("john.doe@example.com") // → "jo****@e****e.com"
MaskUtils.maskCreditCard("4111111111111111") // → "****-****-****-1111"
MaskUtils.maskSsn("123-45-6789")            // → "***-**-6789"
```

### Validation

```java
// Field validation
Validators.email("user@example.com")        // → ValidationResult.ok()
Validators.phone("+18005550100")            // → ValidationResult.ok()
Validators.ssn("123-45-6789")              // → ValidationResult.ok()
Validators.uuid("550e8400-e29b-41d4-a716-446655440000") // → ok

// Schema validation
YamlSchemaValidator.validate(contractYaml) // → List<ValidationError>

// Date range
RangeValidator.dateRange(start, end)       // → ValidationResult
```

### Data Transformation

```java
// Format conversion
String json   = FormatConverter.csvToJson(csvString);
String csv    = FormatConverter.jsonToCsv(jsonString);
String xml    = FormatConverter.jsonToXml(jsonString, "records");

// Type coercions
TypeCoerce.toLocalDate("2024-01-15")       // → LocalDate.of(2024, 1, 15)
TypeCoerce.toBigDecimal("12345.67")        // → BigDecimal("12345.67")
TypeCoerce.toBoolean("true")              // → true
TypeCoerce.toBoolean("yes")               // → true

// Key-path navigation in nested maps
Object value = MapUtils.getPath(record, "user.address.city");  // → "New York"
MapUtils.setPath(record, "user.status", "ACTIVE");
```

### Encoding & Hashing

```java
// Base64
String encoded = EncodeUtils.base64Encode("hello world");
String decoded = EncodeUtils.base64Decode(encoded);

// Hashing
String hash = HashUtils.sha256("sensitive_value");
String md5  = HashUtils.md5("value");

// URL encoding
String safe = EncodeUtils.urlEncode("field value with spaces"); // → "field+value+with+spaces"
```

### Retry & Resilience

```java
// Exponential backoff
RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(3)
    .initialDelay(Duration.ofMillis(200))
    .multiplier(2.0)
    .maxDelay(Duration.ofSeconds(5))
    .build();

RetryUtils.execute(policy, () -> storageService.upload(data));

// Circuit breaker helper
CircuitBreakerState state = CircuitBreakerUtils.evaluate(errorCount, threshold, resetInterval);
```

## Packages

```
com.primus.utils
├── format/               # Formatting utilities
│   ├── MoneyUtils
│   ├── PhoneUtils
│   ├── MaskUtils
│   └── LocaleUtils
├── validation/           # Validators
│   ├── Validators
│   ├── YamlSchemaValidator
│   ├── RangeValidator
│   └── ValidationResult
├── transform/            # Data transformers
│   ├── FormatConverter
│   ├── TypeCoerce
│   └── MapUtils
├── encode/               # Encoding & hashing
│   ├── EncodeUtils
│   └── HashUtils
└── resilience/           # Retry & circuit-breaker
    ├── RetryPolicy
    ├── RetryUtils
    └── CircuitBreakerUtils
```

## Dependencies

- **primus-common** – uses shared DTOs and exceptions
- External:
  - Lombok
  - Jackson (format conversion)
  - SnakeYAML (YAML validation)
  - libphonenumber (phone normalisation)

## Configuration

No Spring beans required. All utilities are pure static helpers (except `YamlSchemaValidator`, which loads schema files from the classpath).

## Extension Points

### Adding a Custom Validator

```java
// Location: com.primus.utils.validation

public class MyFieldValidator {
    public static ValidationResult validate(String value) {
        if (value == null || value.isBlank()) {
            return ValidationResult.error("Value must not be blank");
        }
        // … custom rules …
        return ValidationResult.ok();
    }
}
```

### Adding a Custom Format Converter

```java
// Location: com.primus.utils.transform

public class ParquetConverter {
    public static byte[] fromJson(String json) {
        // Convert JSON → Parquet bytes
    }
}
```

## Future Enhancements

- [ ] Parquet format support in `FormatConverter`
- [ ] Regex-based field masking profiles (configurable)
- [ ] AI-based sensitive-field detection helper
- [ ] Distributed-tracing correlation ID utilities
- [ ] Binary/protobuf conversion helpers

## Usage Example

```java
// Exporting masked customer data
public Map<String, Object> buildExportRow(Customer customer) {
    return Map.of(
        "email",        MaskUtils.maskEmail(customer.getEmail()),
        "phone",        PhoneUtils.normalize(customer.getPhone()),
        "cardNumber",   MaskUtils.maskCreditCard(customer.getCardNumber()),
        "balance",      MoneyUtils.format(customer.getBalance(), "USD", Locale.US),
        "createdAt",    DateUtils.formatDate(customer.getCreatedAt(), "yyyy-MM-dd")
    );
}
```

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-utils:test
```

## Build & Publish

```bash
./gradlew :primus-platform:primus-libs:primus-utils:build
./gradlew :primus-platform:primus-libs:primus-utils:publishToMavenLocal
```

---

**Related:**
- [primus-common.md](./primus-common.md) – base utilities and exceptions
- [primus-annotations.md](./primus-annotations.md) – annotation framework that validates via utils
