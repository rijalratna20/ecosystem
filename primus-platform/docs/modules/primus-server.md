# primus-server Module

## Overview

Main Primus microservice implementing core business logic and REST API endpoints.

## Module Location

```
primus-platform/primus-services/primus/
```

## Purpose

Central service handling:
- Contract management
- Export initiation and monitoring
- Data retrieval operations
- API gateway functionality
- Request routing and validation

## Core Endpoints

### Contract Management

```
GET    /api/v1/contracts                 - List contracts
GET    /api/v1/contracts/{name}          - Get contract details
GET    /api/v1/contracts/{name}/versions - List versions
POST   /api/v1/contracts                 - Create contract
PUT    /api/v1/contracts/{name}          - Update contract
DELETE /api/v1/contracts/{name}          - Delete contract
```

### Export Operations

```
POST   /api/v1/exports                   - Create export
GET    /api/v1/exports                   - List exports
GET    /api/v1/exports/{id}              - Get export details
GET    /api/v1/exports/{id}/status       - Get export status
GET    /api/v1/exports/{id}/retrieve     - Retrieve export data
DELETE /api/v1/exports/{id}              - Delete export
POST   /api/v1/exports/{id}/cancel       - Cancel export
```

### Data Operations

```
GET    /api/v1/data/query                - Query data
GET    /api/v1/data/records/{id}         - Get record
POST   /api/v1/data/records              - Create record
PUT    /api/v1/data/records/{id}         - Update record
DELETE /api/v1/data/records/{id}         - Delete record
```

## Key Services

### ContractService

```java
@Service
public class ContractService {
    
    public List<Contract> listContracts(ContractFilter filter);
    public Contract getContract(String name, String version);
    public Contract createContract(ContractDefinition definition);
    public Contract updateContract(String name, ContractDefinition definition);
    public void deleteContract(String name);
    public List<String> getVersions(String name);
}
```

### ExportService

```java
@Service
public class ExportService {
    
    public Export initiateExport(ExportRequest request);
    public Export getExport(String exportId);
    public List<Export> listExports(ExportFilter filter);
    public ExportStatus getExportStatus(String exportId);
    public byte[] retrieveExportData(String exportId);
    public void deleteExport(String exportId);
    public void cancelExport(String exportId);
}
```

### DataService

```java
@Service
public class DataService {
    
    public List<Map<String, Object>> queryData(
        String contractName, 
        Map<String, Object> filters
    );
    public void createRecord(String contractName, Map<String, Object> data);
    public void updateRecord(String recordId, Map<String, Object> data);
    public void deleteRecord(String recordId);
}
```

## Configuration

```yaml
spring:
  application:
    name: primus-server
  server:
    port: 8080
    ssl:
      enabled: true
      keyStore: classpath:keystore.jks
  
  datasource:
    url: jdbc:postgresql://localhost:5432/primus
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate

primus:
  api:
    version: v1
    baseUrl: https://api.primus.company.com
  
  export:
    batchSize: 1000
    maxConcurrent: 10
    asyncEnabled: true
  
  storage:
    default: s3
    backends: [nas, s3, postgres]
  
  auth:
    jwtSecret: ${JWT_SECRET}
    jwtExpiration: 3600
```

## Request/Response Format

### Standard Response

```json
{
  "status": "SUCCESS",
  "code": 200,
  "data": { ... },
  "metadata": {
    "timestamp": "2024-01-15T10:30:45Z",
    "requestId": "req_xxxxx"
  }
}
```

### Error Response

```json
{
  "status": "ERROR",
  "code": 400,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": { ... }
  }
}
```

## Middleware & Filters

### Authentication Filter

```java
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String token = extractToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            // Set user context
        }
        filterChain.doFilter(request, response);
    }
}
```

### Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        ValidationException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
        Exception e) {
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse("Internal server error"));
    }
}
```

## Monitoring & Metrics

### Available Metrics

```
primus_exports_created_total
primus_exports_completed_total
primus_exports_failed_total
primus_export_duration_seconds
primus_storage_operations_total
primus_api_requests_total
primus_api_request_duration_seconds
```

### Health Endpoints

```
GET  /actuator/health                - Overall health
GET  /actuator/health/db             - Database health
GET  /actuator/health/storage        - Storage backend health
GET  /actuator/metrics               - Metrics list
GET  /actuator/metrics/{metric}      - Specific metric
```

## Logging

```yaml
logging:
  level:
    com.primus: DEBUG
    org.springframework.web: INFO
  file:
    name: /var/log/primus/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

## Testing

```bash
./gradlew :primus-platform:primus-services:primus:test

# Integration tests
./gradlew :primus-platform:primus-services:primus:integrationTest

# Load testing
./gradlew :primus-platform:primus-services:primus:loadTest
```

## Deployment

See [Deployment Guide](../12-deployment.md) for containerization and Kubernetes deployment options.

