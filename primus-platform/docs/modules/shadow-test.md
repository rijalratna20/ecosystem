# shadow-test Module

## Purpose

Dedicated testing and validation service for the Primus platform. Runs contract conformance tests, export/retrieval integration tests, and performance benchmarks against a live (or embedded) Primus environment. Also provides test data generators and a lightweight shadow server that can replay real export requests to validate new deployments before traffic cutover.

## Module Location

```
primus-platform/primus-services/shadow-test/
```

## Responsibilities

✓ **Contract Conformance Testing**
- Parse all registered contracts and validate their schema completeness
- Verify that every contract field has a valid transformer or default
- Detect breaking changes between contract versions

✓ **Integration Test Runner**
- Execute end-to-end export and retrieval flows against a target environment
- Assert response structure, field masking, and data integrity
- Generate structured test reports (JUnit XML, HTML)

✓ **Shadow / Canary Validation**
- Replay captured production export requests against a new deployment
- Compare responses between old and new deployments (diff)
- Flag regressions or unexpected behavior changes

✓ **Test Data Generation**
- Generate synthetic, realistic-looking test records for any registered contract
- Support parameterized data sets (scale, locale, field distributions)
- Provide seed data for development environments

✓ **Performance Benchmarking**
- Run configurable load tests against Primus export and retrieval endpoints
- Measure p50 / p95 / p99 latency and throughput
- Report storage backend performance under load

## Public APIs

### ContractConformanceRunner

```java
public interface ContractConformanceRunner {
    /** Validate all registered contracts. */
    ConformanceReport runAll();

    /** Validate a specific contract version. */
    ConformanceReport run(String contractName, String version);
}

public class ConformanceReport {
    private int contractsTested;
    private int passed;
    private int failed;
    private List<ConformanceFailure> failures;
    private Instant ranAt;
    private Duration duration;
}

public class ConformanceFailure {
    private String contractName;
    private String version;
    private String field;
    private String rule;
    private String message;
}
```

### IntegrationTestRunner

```java
public interface IntegrationTestRunner {
    /** Run all integration test scenarios. */
    TestSuiteResult runAll(TestEnvironment env);

    /** Run a named scenario. */
    TestResult run(String scenarioName, TestEnvironment env);
}

public class TestEnvironment {
    private String primusServerUrl;
    private String apiKey;
    private boolean createTestData;   // Auto-generate seed records
}

public class TestSuiteResult {
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private List<TestResult> results;
    private Path reportPath;
}
```

### ShadowComparator

```java
public interface ShadowComparator {
    /**
     * Replay a set of recorded requests against the target environment
     * and compare results to the baseline.
     */
    ShadowReport compare(
        List<RecordedRequest> baseline,
        TestEnvironment target,
        CompareOptions options
    );
}

public class ShadowReport {
    private int requestsReplayed;
    private int matched;
    private int diverged;
    private List<Divergence> divergences;
}

public class Divergence {
    private RecordedRequest request;
    private Map<String, Object> baselineResponse;
    private Map<String, Object> targetResponse;
    private List<String> diffs;
}

public class CompareOptions {
    private Set<String> ignoreFields;       // Fields to exclude from comparison
    private boolean ignoreTimestamps;
    private boolean ignoreOrderInArrays;
}
```

### TestDataGenerator

```java
public interface TestDataGenerator {
    /** Generate N synthetic records for a given contract. */
    List<Map<String, Object>> generate(String contractName, int count);

    /** Generate with specific field overrides. */
    List<Map<String, Object>> generate(String contractName, int count, Map<String, Object> overrides);

    /** Generate and insert directly into primus-server. */
    SeedResult seed(String contractName, int count, TestEnvironment env);
}
```

### BenchmarkRunner

```java
public interface BenchmarkRunner {
    BenchmarkResult runExportBenchmark(BenchmarkConfig config);
    BenchmarkResult runRetrievalBenchmark(BenchmarkConfig config);
}

public class BenchmarkConfig {
    private String contractName;
    private int recordCount;
    private int concurrentUsers;
    private Duration duration;
    private TestEnvironment env;
}

public class BenchmarkResult {
    private double throughputRps;
    private Duration p50Latency;
    private Duration p95Latency;
    private Duration p99Latency;
    private int errorCount;
    private int successCount;
}
```

## Packages

```
com.primus.shadow.test
├── conformance/           # Contract conformance testing
│   ├── ContractConformanceRunner
│   ├── ConformanceReport
│   └── ConformanceRules
├── integration/           # End-to-end integration tests
│   ├── IntegrationTestRunner
│   ├── TestScenario
│   ├── TestEnvironment
│   └── TestSuiteResult
├── shadow/                # Shadow replay & comparison
│   ├── ShadowComparator
│   ├── RequestRecorder
│   ├── RequestReplayer
│   └── ShadowReport
├── datagen/               # Synthetic test data
│   ├── TestDataGenerator
│   ├── FieldValueFactory
│   └── SeedResult
├── benchmark/             # Performance benchmarking
│   ├── BenchmarkRunner
│   ├── BenchmarkConfig
│   └── BenchmarkResult
└── report/                # Report formatting
    ├── JUnitXmlReporter
    ├── HtmlReporter
    └── ConsoleReporter
```

## Dependencies

- **primus-client-sdk** – communicates with Primus server under test
- **primus-common** – shared DTOs
- **primus-core** – contract model (for conformance analysis)
- External:
  - JUnit 5 (test scaffolding)
  - Testcontainers (spin up Primus environment in Docker)
  - WireMock (mock external dependencies)
  - JavaFaker (synthetic data generation)
  - Apache JMeter API (benchmarking)

## Configuration

```yaml
primus:
  shadow-test:
    target:
      url: http://localhost:8080
      api-key: ${PRIMUS_TEST_API_KEY}

    conformance:
      fail-on-warning: false

    shadow:
      ignore-fields:
        - requestId
        - timestamp
        - correlationId
      ignore-timestamps: true

    datagen:
      locale: en-US
      default-count: 100
      seed: 42             # Reproducible random data

    benchmark:
      concurrent-users: 10
      duration: 60s
```

## Usage Examples

### Run Conformance Tests

```bash
# Via Gradle
./gradlew :primus-platform:primus-services:shadow-test:conformance \
  --args="--target http://primus-dev:8080 --api-key $PRIMUS_API_KEY"

# Output:
# Contracts tested: 12  |  Passed: 11  |  Failed: 1
# FAIL  CustomerData v2.0: field 'ssn' missing required transformer
```

### Run Integration Tests

```bash
./gradlew :primus-platform:primus-services:shadow-test:integrationTest \
  --args="--env development"

# Generates:
#   build/reports/shadow-test/test-results.xml  (JUnit XML)
#   build/reports/shadow-test/report.html       (HTML report)
```

### Shadow Comparison

```bash
./gradlew :primus-platform:primus-services:shadow-test:shadow \
  --args="--baseline https://primus-prod:8080 --target https://primus-canary:8080 \
          --replay-file requests.ndjson"
```

### Generate Seed Data

```bash
./gradlew :primus-platform:primus-services:shadow-test:seed \
  --args="--contract LoanData --count 500 --env local"
```

### Run Benchmarks

```bash
./gradlew :primus-platform:primus-services:shadow-test:benchmark \
  --args="--contract CustomerData --records 10000 --users 20 --duration 120s"

# Output:
# Throughput: 450 req/s
# p50:  45ms  |  p95:  120ms  |  p99:  380ms
# Errors: 0 / 54000
```

## CI/CD Integration

Add shadow-test to your deployment pipeline to gate releases:

```yaml
# GitHub Actions example
- name: Run Primus Conformance Tests
  run: |
    ./gradlew :primus-platform:primus-services:shadow-test:conformance \
      --args="--target ${{ env.PRIMUS_STAGING_URL }}"

- name: Run Shadow Comparison
  run: |
    ./gradlew :primus-platform:primus-services:shadow-test:shadow \
      --args="--baseline ${{ env.PRIMUS_PROD_URL }} \
              --target ${{ env.PRIMUS_CANARY_URL }} \
              --replay-file recorded-requests.ndjson"
```

## Extension Points

### Custom Test Scenario

```java
@ShadowTestScenario("sensitive-field-masking")
public class SensitiveFieldMaskingScenario implements TestScenario {

    @Override
    public TestResult run(PrimusClient client) {
        ExportResponse response = client.export()
            .contract("CustomerData")
            .filter("status", "ACTIVE")
            .execute();

        // Assert SSN is masked
        Page<Map<String, Object>> page = client.retrieve()
            .exportId(response.getExportId())
            .page(0).pageSize(1).execute();

        String ssn = (String) page.getContent().get(0).get("ssn");
        assertThat(ssn).matches("\\*\\*\\*-\\*\\*-\\d{4}");

        return TestResult.passed();
    }
}
```

### Custom Data Generator

```java
@Component
public class LoanDataGenerator implements FieldValueFactory {

    @Override
    public boolean supports(String contractName, String fieldName) {
        return "LoanData".equals(contractName) && "loanAmount".equals(fieldName);
    }

    @Override
    public Object generate(String contractName, String fieldName) {
        // Generate realistic loan amounts
        return BigDecimal.valueOf(10000 + ThreadLocalRandom.current().nextInt(490000));
    }
}
```

## Future Enhancements

- [ ] Mutation testing (intentionally corrupt requests to verify error handling)
- [ ] Fuzz testing support for YAML contract parsing
- [ ] Replay from Kafka topic
- [ ] Automated regression baseline capture from production traffic
- [ ] Visual diff UI for shadow comparison reports

## Build & Run

```bash
./gradlew :primus-platform:primus-services:shadow-test:build
./gradlew :primus-platform:primus-services:shadow-test:test
```

---

**Related:**
- [shadow-server.md](./shadow-server.md) – runtime shadow server that this module tests against
- [primus-client-sdk.md](./primus-client-sdk.md) – client used to communicate with Primus
- [10-rest-api.md](../10-rest-api.md) – API endpoints exercised by integration tests
- [12-deployment.md](../12-deployment.md) – deployment environments
