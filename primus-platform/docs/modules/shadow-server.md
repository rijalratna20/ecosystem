# shadow-server Module

## Overview

Testing and validation microservice for Primus platform experiments and validation scenarios.

## Module Location

```
primus-platform/primus-services/shadow-test/
```

## Purpose

Shadow Server serves as an alternative/experimental backend for:
- A/B testing new features
- Performance benchmarking
- Compatibility testing
- Data validation sandbox
- Feature experiments

## Key Endpoints

### Experiment Management

```
POST   /api/v1/experiments              - Create experiment
GET    /api/v1/experiments/{id}         - Get experiment details
GET    /api/v1/experiments              - List experiments
PUT    /api/v1/experiments/{id}         - Update experiment
DELETE /api/v1/experiments/{id}         - Delete experiment
POST   /api/v1/experiments/{id}/toggle  - Enable/disable experiment
```

### Test Data

```
POST   /api/v1/test-data               - Generate test data
POST   /api/v1/test-data/import        - Import test dataset
GET    /api/v1/test-data/contracts     - List test contracts
DELETE /api/v1/test-data/{id}          - Clear test data
```

### Validation

```
POST   /api/v1/validate/contract       - Validate contract definition
POST   /api/v1/validate/data           - Validate data against contract
POST   /api/v1/validate/export         - Test export flow
```

### Performance

```
POST   /api/v1/benchmark               - Run performance benchmark
GET    /api/v1/benchmark/{id}/results  - Get benchmark results
POST   /api/v1/benchmark/{id}/cancel   - Cancel benchmark
```

## Services

### ExperimentService

```java
@Service
public class ExperimentService {
    
    public Experiment createExperiment(ExperimentDefinition def);
    public Experiment getExperiment(String experimentId);
    public List<Experiment> listExperiments(ExperimentFilter filter);
    public void updateExperiment(String experimentId, ExperimentDefinition def);
    public void deleteExperiment(String experimentId);
    public void toggleExperiment(String experimentId, boolean enabled);
}
```

### ValidationService

```java
@Service
public class ValidationService {
    
    public ValidationResult validateContract(ContractDefinition contract);
    public ValidationResult validateData(Map<String, Object> data, Contract contract);
    public ExportValidationResult testExportFlow(ExportRequest request);
    public List<ValidationError> findDataIssues(String contractName);
}
```

### BenchmarkService

```java
@Service
public class BenchmarkService {
    
    public Benchmark startBenchmark(BenchmarkConfig config);
    public Benchmark getBenchmarkResults(String benchmarkId);
    public void cancelBenchmark(String benchmarkId);
    public ComparisonReport comparePrimary(String benchmarkId);
}
```

## Experiment Definition

```yaml
experiment:
  name: "new-export-format"
  version: "1.0"
  description: "Test new Parquet export format"
  
  targetPercentage: 10          # Route 10% of traffic
  enabled: true
  
  configuration:
    exportFormat: "PARQUET"     # New format to test
    compressionType: "snappy"
    batchSize: 5000
  
  rollout:
    startDate: "2024-01-15"
    endDate: "2024-02-15"
```

## Test Data Generation

```java
@PostMapping("/api/v1/test-data")
public ResponseEntity<TestData> generateTestData(
    @RequestBody TestDataRequest request) {
    
    TestDataGenerator generator = new TestDataGenerator();
    TestData testData = generator
        .forContract(request.getContractName())
        .withRecordCount(request.getRecordCount())
        .usingFaker()
        .generate();
    
    return ResponseEntity.ok(testData);
}
```

## Validation Examples

### Contract Validation

```bash
curl -X POST http://localhost:8081/api/v1/validate/contract \
  -H "Content-Type: application/yaml" \
  -d '
  name: UserProfile
  version: "1.0"
  fields:
    - name: userId
      type: UUID
      required: true
'
```

### Data Validation

```bash
curl -X POST http://localhost:8081/api/v1/validate/data \
  -H "Content-Type: application/json" \
  -d '{
    "contractName": "UserProfile",
    "data": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com"
    }
  }'
```

## Performance Benchmarking

### Benchmark Configuration

```java
public class BenchmarkConfig {
    private String contractName;
    private int recordCount;           // Records to process
    private int batchSize;             // Batch size
    private int numberOfThreads;       // Threads
    private long durationSeconds;      // Duration
    private List<String> targets;      // Endpoints to test
}
```

### Running Benchmark

```bash
# Start benchmark
curl -X POST http://localhost:8081/api/v1/benchmark \
  -H "Content-Type: application/json" \
  -d '{
    "contractName": "UserProfile",
    "recordCount": 100000,
    "batchSize": 1000,
    "numberOfThreads": 10,
    "durationSeconds": 300
  }'

# Get results
curl http://localhost:8081/api/v1/benchmark/{benchmarkId}/results
```

## Results & Metrics

### Benchmark Results

```json
{
  "benchmarkId": "bench_20240115_001",
  "status": "COMPLETED",
  "metrics": {
    "totalRequests": 1500,
    "successfulRequests": 1485,
    "failedRequests": 15,
    "p50Latency": 125,
    "p95Latency": 450,
    "p99Latency": 1200,
    "throughput": 5.0,
    "averageLatency": 200
  },
  "comparisonWithPrimary": {
    "latencyDifference": "+5%",
    "throughputDifference": "-2%",
    "errorRateDifference": "+0.5%"
  }
}
```

## Configuration

```yaml
spring:
  application:
    name: shadow-test

server:
  port: 8081

shadow:
  primary:
    baseUrl: "http://primus-server:8080"
  
  experiments:
    enabled: true
    trafficSplitPercentage: 5
  
  dataGeneration:
    faker:
      enabled: true
      locale: en_US
  
  benchmarking:
    threadPoolSize: 20
    timeoutSeconds: 600
```

## Testing

```bash
./gradlew :primus-platform:primus-services:shadow-test:test
```

## Use Cases

1. **Feature Testing**: Test new export formats safely
2. **Load Testing**: Benchmark system performance
3. **Data Quality**: Validate data issues
4. **A/B Testing**: Compare implementations
5. **Contract Evolution**: Test contract changes

