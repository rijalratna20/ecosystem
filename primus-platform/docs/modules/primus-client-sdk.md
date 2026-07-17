# primus-client-sdk Module

## Overview

SDK for integrating Primus platform services into client applications.

## Module Location

```
primus-platform/primus-libs/primus-client-sdk/
```

## Purpose

Provides a high-level API client for consuming Primus services, handling authentication, pagination, error handling, and serialization automatically.

## Key Features

- **Auto-generated REST clients** from contracts
- **Built-in retry logic** for resilience
- **Pagination support** for large datasets
- **Streaming** for memory efficiency
- **Caching** for performance
- **Type-safe** API with generics
- **Async/await** support

## Installation

### Maven

```xml
<dependency>
    <groupId>com.primus</groupId>
    <artifactId>primus-client-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.primus:primus-client-sdk:1.0.0'
```

## Configuration

```java
PrimusClient client = new PrimusClientBuilder()
    .baseUrl("https://api.primus.company.com")
    .apiKey("sk_live_xxxxx")
    .readTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(10, TimeUnit.SECONDS)
    .retryPolicy(new RetryPolicy().withMaxRetries(3))
    .build();
```

### Configuration File

```yaml
primus:
  client:
    baseUrl: "https://api.primus.company.com"
    apiKey: "${PRIMUS_API_KEY}"
    readTimeout: 30000
    connectTimeout: 10000
    retry:
      maxRetries: 3
      backoffMultiplier: 2
```

## API Usage

### Export Operations

```java
// Create export
ExportRequest request = new ExportRequest()
    .contractName("UserProfile")
    .contractVersion("1.0")
    .format("JSON")
    .destination("s3");

Export export = client.exports().create(request);

// Get export status
Export status = client.exports().getStatus(export.getExportId());

// List exports
Page<Export> exports = client.exports()
    .list(new ExportFilter().status("COMPLETED"))
    .pageSize(50)
    .execute();

// Delete export
client.exports().delete(export.getExportId());
```

### Data Retrieval

```java
// Simple retrieval
List<Map<String, Object>> records = client.data()
    .retrieve("exp_20240115_001")
    .format("JSON")
    .execute();

// Paginated retrieval
Page<Map<String, Object>> page = client.data()
    .retrieve("exp_20240115_001")
    .paginated()
    .pageSize(100)
    .pageNumber(1)
    .execute();

// Streaming retrieval
client.data()
    .retrieve("exp_20240115_001")
    .streaming()
    .onRecord(record -> System.out.println(record))
    .onError(error -> System.err.println(error))
    .execute();

// With filtering
List<Map<String, Object>> filtered = client.data()
    .retrieve("exp_20240115_001")
    .filter("status", "ACTIVE")
    .filter("tier", IN, Arrays.asList("PREMIUM", "ENTERPRISE"))
    .execute();
```

### Contract Operations

```java
// Get contract definition
Contract contract = client.contracts()
    .get("UserProfile", "1.0");

// List all contracts
List<Contract> contracts = client.contracts()
    .list(new ContractFilter().namespace("com.primus.user"))
    .execute();

// Create new contract
Contract newContract = client.contracts()
    .create(new ContractDefinition()
        .name("NewContract")
        .version("1.0")
        .namespace("com.primus.new")
        .fields(fields)
    );
```

## Error Handling

```java
try {
    Export export = client.exports().create(request);
} catch (AuthenticationException e) {
    System.err.println("Invalid credentials: " + e.getMessage());
} catch (AuthorizationException e) {
    System.err.println("Insufficient permissions: " + e.getMessage());
} catch (ValidationException e) {
    System.err.println("Validation failed: " + e.getMessage());
    e.getErrors().forEach(System.err::println);
} catch (PrimusException e) {
    System.err.println("Error: " + e.getMessage());
}
```

## Async Operations

```java
// Using CompletableFuture
client.exports().createAsync(request)
    .thenAccept(export -> System.out.println("Export: " + export))
    .exceptionally(error -> {
        System.err.println("Error: " + error.getMessage());
        return null;
    });

// Using callbacks
client.exports().create(request, new Callback<Export>() {
    @Override
    public void onSuccess(Export export) {
        System.out.println("Export: " + export);
    }
    
    @Override
    public void onError(Exception error) {
        System.err.println("Error: " + error);
    }
});
```

## Advanced Features

### Retry Logic

```java
client.exports()
    .create(request)
    .withRetry(new RetryPolicy()
        .maxRetries(5)
        .backoffMultiplier(2.0)
        .maxBackoffMs(30000)
        .retryOn(TimeoutException.class, IOException.class)
    );
```

### Caching

```java
client.contracts()
    .get("UserProfile", "1.0")
    .withCache(Duration.ofHours(1))
    .execute();
```

### Request Interceptors

```java
client.addInterceptor(new HttpInterceptor() {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Add custom headers
        Request newRequest = originalRequest.newBuilder()
            .addHeader("X-Request-ID", UUID.randomUUID().toString())
            .build();
        
        return chain.proceed(newRequest);
    }
});
```

## Testing

```gradle
testImplementation 'com.primus:primus-client-sdk:1.0.0'
testImplementation 'org.mockito:mockito-core:5.0.0'
```

### Mock Client

```java
@Before
public void setup() {
    mockClient = new MockPrimusClient();
    mockClient.exports()
        .mockCreate(new Export().exportId("exp_mock_001"));
}

@Test
public void testExportCreation() {
    Export export = mockClient.exports()
        .create(request);
    
    assertEquals("exp_mock_001", export.getExportId());
}
```

## Changelog

### Latest Changes
- **v1.0.0**: Initial release
- Streaming API support
- Async operations
- Retry logic
- Request caching

## Support

- **Documentation**: https://docs.primus.company.com
- **Issues**: https://github.com/primus/primus-client-sdk/issues
- **Email**: support@primus.company.com

