# spring-client Module

## Purpose

Ready-to-use Spring Boot auto-configuration and integration beans that allow any Spring-based application to communicate with the Primus platform with minimal boilerplate. Applications add this module to their classpath and get a fully configured `PrimusClient` instance via Spring dependency injection.

## Module Location

```
primus-platform/primus-services/spring-client/
```

## Responsibilities

✓ **Auto-Configuration**
- Detect `primus.*` properties in `application.yaml` / `application.properties`
- Create and register a `PrimusClient` bean automatically
- Configure HTTP connection pool, retry, and timeout settings
- Conditionally enable features (export, retrieval, streaming)

✓ **REST Template / WebClient Integration**
- Provide pre-configured `RestTemplate` / `WebClient` instances for Primus endpoints
- Attach JWT bearer token to every outgoing request
- Handle token refresh transparently

✓ **Spring Security Integration**
- Expose `PrimusAuthenticationProvider` for use in Spring Security filter chains
- Validate inbound Primus-issued JWTs (for services acting as resource servers)
- Propagate the authenticated principal to Primus API calls

✓ **Health & Actuator Integration**
- Register a `PrimusHealthIndicator` for Spring Boot Actuator
- Expose `/actuator/health/primus` showing connectivity to the Primus server

✓ **Event Listener Helpers**
- Provide `@PrimusEventListener` abstraction for consuming export / retrieval events
- Bridge internal Primus events to Spring Application Events

## Public APIs

### Auto-Configuration

```java
// Simply add the dependency and set properties – no code required.
// Bean is created automatically:

@Autowired
private PrimusClient primusClient;

// Use it directly:
ExportResponse response = primusClient.export()
    .contract("UserProfile")
    .filter("lastModified", ">", "2024-01-01")
    .execute();
```

### PrimusClientBuilder (manual configuration)

```java
// For non-Spring environments or advanced customisation:
PrimusClient client = PrimusClient.builder()
    .serverUrl("https://primus.example.com")
    .apiKey(System.getenv("PRIMUS_API_KEY"))
    .connectTimeout(Duration.ofSeconds(10))
    .readTimeout(Duration.ofSeconds(60))
    .maxRetries(3)
    .build();
```

### Export Support

```java
// Fluent export API
ExportResponse response = primusClient.export()
    .contract("LoanData")
    .version("2.1")
    .filter("region", "US")
    .filter("status", "APPROVED")
    .format(ExportFormat.JSON)
    .execute();

String exportId = response.getExportId();
ExportStatus status = primusClient.exports().getStatus(exportId);
```

### Retrieval Support

```java
// Paginated retrieval
Page<Map<String, Object>> page = primusClient.retrieve()
    .exportId("exp_20240115_001")
    .page(0)
    .pageSize(500)
    .fields("customerId", "loanAmount", "status")
    .execute();

// Streaming retrieval
primusClient.retrieve()
    .exportId("exp_20240115_001")
    .stream()
    .forEach(record -> process(record));
```

### Health Indicator

```java
// Automatically registered – visible at /actuator/health
{
  "status": "UP",
  "components": {
    "primus": {
      "status": "UP",
      "details": {
        "serverUrl": "https://primus.example.com",
        "responseTimeMs": 45,
        "contractsAvailable": 12
      }
    }
  }
}
```

### Event Listener

```java
@Component
public class ExportCompletionHandler {

    @PrimusEventListener(ExportCompletedEvent.class)
    public void onExportCompleted(ExportCompletedEvent event) {
        log.info("Export {} completed: {} records", event.getExportId(), event.getRecordCount());
        notificationService.notify(event);
    }
}
```

## Packages

```
com.primus.spring.client
├── autoconfigure/         # Spring Boot auto-configuration
│   ├── PrimusAutoConfiguration
│   ├── PrimusClientProperties       # Binds primus.* properties
│   └── PrimusSecurityAutoConfiguration
├── client/                # HTTP client adapters
│   ├── PrimusRestClient
│   ├── PrimusWebClient
│   └── RetryHandler
├── export/                # Export API helpers
│   ├── ExportRequestBuilder
│   └── ExportResponseParser
├── retrieval/             # Retrieval API helpers
│   ├── RetrievalRequestBuilder
│   └── RetrievalStream
├── health/                # Actuator integration
│   └── PrimusHealthIndicator
├── security/              # Spring Security integration
│   └── PrimusAuthenticationProvider
└── event/                 # Event bridge
    ├── PrimusEventListener (annotation)
    └── PrimusEventBridge
```

## Dependencies

- **primus-client-sdk** – core client interfaces and models
- **primus-common** – shared DTOs and exceptions
- External:
  - Spring Boot (auto-configuration)
  - Spring Security (JWT resource server)
  - Spring Boot Actuator
  - Spring WebFlux (optional, for reactive WebClient)

## Configuration

```yaml
primus:
  client:
    server-url: https://primus.example.com     # Required
    api-key: ${PRIMUS_API_KEY}                 # Required (or use oauth2)
    connect-timeout: 10s
    read-timeout: 60s
    max-retries: 3
    retry-delay: 500ms

    oauth2:
      enabled: false
      token-uri: ${OAUTH2_TOKEN_URI}
      client-id: ${OAUTH2_CLIENT_ID}
      client-secret: ${OAUTH2_CLIENT_SECRET}

    export:
      default-format: JSON
      default-page-size: 1000

    actuator:
      enabled: true                            # Register health indicator
```

## Getting Started

### 1. Add Dependency

```gradle
// build.gradle
dependencies {
    implementation project(':primus-platform:primus-services:spring-client')
}
```

### 2. Set Properties

```yaml
# application.yaml
primus:
  client:
    server-url: https://primus.example.com
    api-key: ${PRIMUS_API_KEY}
```

### 3. Inject and Use

```java
@Service
public class DataExportService {

    @Autowired
    private PrimusClient primusClient;

    public String exportCustomerData(String region) {
        ExportResponse response = primusClient.export()
            .contract("CustomerData")
            .filter("region", region)
            .execute();
        return response.getExportId();
    }
}
```

## Extension Points

### Custom Request Interceptor

```java
@Component
public class CorrelationIdInterceptor implements PrimusRequestInterceptor {
    @Override
    public void intercept(PrimusRequest request) {
        request.header("X-Correlation-ID", MDC.get("correlationId"));
    }
}
```

### Custom Error Handler

```java
@Component
public class PrimusErrorHandler implements PrimusResponseErrorHandler {
    @Override
    public void handleError(PrimusErrorResponse error) {
        if (error.getCode() == 429) {
            throw new RateLimitExceededException(error.getMessage());
        }
    }
}
```

## Future Enhancements

- [ ] Reactive (Flux/Mono) streaming for large exports
- [ ] GraphQL client support
- [ ] gRPC transport option
- [ ] Circuit breaker integration (Resilience4j)
- [ ] Micrometer metrics for export / retrieval operations
- [ ] Spring Batch `ItemReader` / `ItemWriter` integration

## Testing

```bash
./gradlew :primus-platform:primus-services:spring-client:test
```

Tests use a WireMock server to simulate Primus API responses. Key scenarios:
- Auto-configuration with minimal properties
- Token refresh on 401 response
- Retry on transient 5xx errors
- Health indicator UP/DOWN transitions

## Build & Publish

```bash
./gradlew :primus-platform:primus-services:spring-client:build
./gradlew :primus-platform:primus-services:spring-client:publishToMavenLocal
```

---

**Related:**
- [primus-client-sdk.md](./primus-client-sdk.md) – core SDK that this module wraps
- [10-rest-api.md](../10-rest-api.md) – underlying REST endpoints
- [06-export-flow.md](../06-export-flow.md) – export workflow
- [07-retrieval-flow.md](../07-retrieval-flow.md) – retrieval workflow
