# primus-sdk

Java client SDK for Primus Platform API.

## Overview

`primus-sdk` is the official Java client library for communicating with Primus services. It provides type-safe access to export, retrieval, and metadata APIs.

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-sdk')
}
```

### Initialize Client

```java
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .apiKey("your-api-key")
    .applicationId("CustomerService")
    .build();
```

### Export Data

```java
ExportRequest request = ExportRequest.builder()
    .application("CustomerService")
    .collections(Arrays.asList("customers", "accounts"))
    .filter("customerId = '12345'")
    .build();

ExportResponse response = client.export(request);
System.out.println("Export ID: " + response.getExportId());
System.out.println("Status: " + response.getStatus());
```

### Retrieve Data

```java
RetrievalRequest request = RetrievalRequest.builder()
    .application("CustomerService")
    .exportId("exp_123456")
    .build();

RetrievalResponse response = client.retrieve(request);
CustomerData data = response.getData();
```

## Key APIs

### PrimusClient

#### Export Operations
```java
ExportResponse export(ExportRequest request)
List<ExportResponse> listExports(String application)
ExportResponse getExportStatus(String exportId)
```

#### Retrieval Operations
```java
RetrievalResponse retrieve(RetrievalRequest request)
List<RetrievalResponse> listRetrievals(String application)
```

#### Metadata Operations
```java
ApplicationContract getApplicationMetadata(String application)
List<String> listApplications()
```

## Configuration

### Authentication

#### API Key
```java
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .apiKey("sk_live_xyz123")
    .build();
```

#### OAuth2
```java
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .oauth2(new OAuth2Config()
        .clientId("client_id")
        .clientSecret("client_secret"))
    .build();
```

### Client Options
```java
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .apiKey("sk_live_xyz123")
    .connectionTimeout(Duration.ofSeconds(30))
    .readTimeout(Duration.ofSeconds(60))
    .maxRetries(3)
    .enableLogging(true)
    .build();
```

## Testing

```bash
./gradlew :primus-platform:primus-sdk:test
```

## Module Dependencies

- `primus-common`
- `primus-contract`

## Error Handling

```java
try {
    ExportResponse response = client.export(request);
} catch (ValidationException e) {
    System.err.println("Validation error: " + e.getMessage());
} catch (AuthorizationException e) {
    System.err.println("Authorization failed: " + e.getMessage());
} catch (StorageException e) {
    System.err.println("Storage error: " + e.getMessage());
} catch (PrimusException e) {
    System.err.println("Platform error: " + e.getMessage());
}
```

## Examples

### Complete Export + Retrieve Flow
```java
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .apiKey("sk_live_xyz123")
    .build();

// 1. Export data
ExportRequest exportReq = ExportRequest.builder()
    .application("CustomerService")
    .collections(Arrays.asList("customers"))
    .filter("region = 'US'")
    .build();

ExportResponse exportResp = client.export(exportReq);
String exportId = exportResp.getExportId();

// 2. Wait for completion
while (exportResp.getStatus() == ExportStatus.PROCESSING) {
    Thread.sleep(5000);
    exportResp = client.getExportStatus(exportId);
}

// 3. Retrieve the data
if (exportResp.getStatus() == ExportStatus.COMPLETED) {
    RetrievalRequest retrieveReq = RetrievalRequest.builder()
        .application("CustomerService")
        .exportId(exportId)
        .build();
    
    RetrievalResponse retrieveResp = client.retrieve(retrieveReq);
    CustomerData data = retrieveResp.getData();
    System.out.println("Retrieved " + data.size() + " records");
}
```

## Troubleshooting

**Issue**: "Unauthorized" error
- **Solution**: Verify API key is correct and still valid

**Issue**: "Application not found"
- **Solution**: Check application ID matches registered name in Primus

**Issue**: "Collection not found in application"
- **Solution**: Review application metadata to see available collections

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

