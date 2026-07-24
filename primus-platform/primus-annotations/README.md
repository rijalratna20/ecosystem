# primus-annotations

Annotation framework for annotation-driven export and API contracts.

## Overview

`primus-annotations` provides Java annotations that allow developers to declaratively mark classes and methods for export, retrieval, and API operations. Primus processes these annotations at runtime to generate UI, enforce policies, and track data flows.

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-annotations')
}
```

### Using @PrimusExport

```java
@PrimusExport(
    application = "CustomerService",
    collections = {"customers", "accounts"},
    description = "Export customer data with related accounts"
)
public void exportCustomerData(String customerId) {
    // Implementation
}
```

### Using @PrimusRetrieval

```java
@PrimusRetrieval(
    application = "CustomerService",
    targetCollections = {"customers", "accounts", "transactions"}
)
public CustomerData retrieveCustomerTransaction(String transactionId) {
    // Implementation
}
```

## Key Annotations

### @PrimusExport
Marks a method as an export operation:
```java
@PrimusExport(
    application = "LoanService",
    collections = {"loans", "applications", "documents"},
    description = "Export complete loan application",
    sensitive = true
)
public ExportResult exportLoanApplication(String loanId) { }
```

**Attributes:**
- `application` (required) — application name
- `collections` (required) — targeted collections
- `description` — human-readable description
- `sensitive` — triggers approval workflow if true

### @PrimusRetrieval
Marks a method as a retrieval operation:
```java
@PrimusRetrieval(
    application = "PaymentService",
    targetCollections = {"payments", "disputes", "refunds"}
)
public PaymentDetails retrievePaymentDetails(String paymentId) { }
```

### @PrimusSensitive
Marks a field as containing sensitive data:
```java
public class Customer {
    @PrimusSensitive(
        dataClass = "SSN",
        maskPattern = "***-**-####"
    )
    private String ssn;
}
```

### @PrimusAPI
Marks a REST endpoint as part of Primus API contract:
```java
@GetMapping("/export/{id}")
@PrimusAPI(
    endpoint = "/api/v1/export/{id}",
    method = "GET",
    authentication = AuthType.JWT,
    rateLimit = RateLimit.PER_MINUTE_100
)
public ResponseEntity<ExportResponse> getExport(@PathVariable String id) { }
```

## Configuration

Annotations are processed automatically by `primus-server` at runtime. No additional configuration needed.

## Testing

```bash
./gradlew :primus-platform:primus-annotations:test
```

## Module Dependencies

- `primus-common`

## Best Practices

✅ Use `@PrimusExport` on all export methods  
✅ Mark sensitive fields with `@PrimusSensitive`  
✅ Document the collections being exported  
✅ Set `sensitive = true` if data contains PII  
✅ Use consistent application names  

## Sub-modules

### primus-export
Annotations for export operations

### primus-api
Annotations for REST API contracts

### authenticator
Annotations for authentication/authorization

## Troubleshooting

**Issue**: Annotation not being processed
- **Solution**: Ensure primus-server is running and can see the annotation on the bean

**Issue**: Sensitive field not being masked
- **Solution**: Verify `@PrimusSensitive` is present and maskPattern is valid

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

