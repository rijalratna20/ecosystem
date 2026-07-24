# Contributing to Primus Platform

Thank you for contributing! This guide explains how to implement modules using the strategy-driven approach.

---

## 🎯 Understanding Your Task

Every incomplete module has a defined **entry criteria** and **exit criteria** in [strategy.MD](./strategy.MD).

### Entry Criteria = Prerequisites
Before starting, verify all entry criteria are met. Common examples:
- Tier 1 baseline is complete
- Related domain models are approved (e.g., `AuditEvent` schema)
- Dependencies are already implemented

### Exit Criteria = Definition of Done
Your work is complete when ALL exit criteria are satisfied:
- Gradle module builds and is added to `settings.gradle`
- APIs and contracts are documented and contract-tested
- Unit + integration tests pass (including negative paths)
- Observability (metrics, logging, correlation IDs) is implemented
- Security controls are in place
- Operational runbook is added to `primus-platform/docs/`

---

## 🚀 Getting Started

### 1. Find Your Task
```bash
cd /Users/ratnarijal/Desktop/Project/ai-sytem-design

# Activate Python venv
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt

# List ready tasks (those with all dependencies completed)
python tools/strategy_task_router.py --status strategy.status.example.json next --limit 5
```

### 2. Review Your Module Plan
For Tier N work, read:
- `primus-platform/docs/planning/TIER_N_MS_PLAN_AND_TODO.md` — detailed scope
- `strategy.MD` — entry/exit criteria for your specific module
- Related architecture docs in `primus-platform/docs/`

### 3. Generate Implementation Guidance
```bash
# For Copilot/GitHub Copilot
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent copilot

# For Claude Code
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent claude
```

---

## 📝 Implementation Checklist

### Phase 1: Scaffold & Setup
- [ ] Create module directory: `primus-platform/primus-services/{module-name}`
- [ ] Create `build.gradle` (see templates below)
- [ ] Add module to `settings.gradle`
- [ ] Create module structure:
  ```
  {module-name}/
  ├── build.gradle
  ├── src/
  │   ├── main/java/com/primus/{module}/
  │   │   ├── api/
  │   │   ├── domain/
  │   │   ├── persistence/
  │   │   └── {ModuleService}.java
  │   └── test/java/com/primus/{module}/
  │       ├── api/
  │       ├── domain/
  │       └── {ModuleServiceTest}.java
  └── README.md
  ```

### Phase 2: Domain & API
- [ ] Define domain models (entities, DTOs)
- [ ] Define REST API contracts (OpenAPI/Swagger)
- [ ] Define event schemas (if applicable)
- [ ] Write API documentation
- [ ] Add contract tests

### Phase 3: Core Functionality
- [ ] Implement service layer
- [ ] Add persistence layer (JPA/database)
- [ ] Implement business logic
- [ ] Write unit tests
- [ ] Write integration tests

### Phase 4: Observability & Security
- [ ] Add metrics (Micrometer, Prometheus)
- [ ] Add structured logging (SLF4J with MDC)
- [ ] Add correlation ID handling
- [ ] Add security checks (auth, authorization)
- [ ] Add error handling and response normalization

### Phase 5: Documentation & Handoff
- [ ] Create module `README.md` with quick start
- [ ] Create operational runbook: `primus-platform/docs/modules/{module}-ops.md`
- [ ] Add examples and quick-start code
- [ ] Document deployment and configuration
- [ ] Verify all exit criteria checklist is 100%

---

## 📂 build.gradle Template

For a new service module:

```groovy
plugins {
    id 'java'
    id 'spring-boot' version '3.1.0'
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.primus'
version = '0.1.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    // Primus shared libraries
    implementation project(':primus-platform:primus-common')
    implementation project(':primus-platform:primus-security')
    implementation project(':primus-platform:primus-contract')
    
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Database
    implementation 'org.postgresql:postgresql:42.5.0'
    
    // Logging & Observability
    implementation 'io.micrometer:micrometer-registry-prometheus'
    implementation 'org.springframework.boot:spring-boot-starter-logging'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:testcontainers:1.17.0'
    testImplementation 'org.testcontainers:postgresql:1.17.0'
}

test {
    useJUnitPlatform()
}

bootJar {
    archiveFileName = "${project.name}-boot.jar"
}

jar {
    enabled = true
    archiveFileName = "${project.name}.jar"
}
```

For a library module:

```groovy
plugins {
    id 'java-library'
}

group = 'com.primus'
version = '0.1.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    // Shared libraries
    api project(':primus-platform:primus-common')
    
    // Logging
    implementation 'org.slf4j:slf4j-api'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
}

java {
    withSourcesJar()
    withJavadocJar()
}
```

---

## 🧪 Testing Requirements

Every module must have:

### Unit Tests (Happy Path + Negative Paths)
```java
@Test
void shouldSucceedWhenAllInputsValid() {
    // Arrange: set up valid inputs
    // Act: call the function
    // Assert: verify expected behavior
}

@Test
void shouldThrowExceptionWhenInputInvalid() {
    // Arrange: set up invalid inputs
    // Act & Assert: verify exception is thrown
}
```

### Integration Tests (End-to-End Flows)
```java
@SpringBootTest
class ModuleIntegrationTest {
    @Autowired private ModuleService service;
    
    @Test
    void testEndToEndFlow() {
        // Full flow: create -> process -> retrieve -> verify
    }
}
```

### Negative Path Coverage
```
❌ Missing required fields
❌ Invalid input types
❌ Null/empty values
❌ Permission denied scenarios
❌ Dependency failures (network, database timeouts)
❌ Concurrent requests (race conditions)
```

---

## 📊 Observability Requirements

Every module must emit:

### Metrics (Prometheus)
```java
@Component
public class ModuleMetrics {
    private final MeterRegistry meterRegistry;
    
    public void recordOperation(String name, long durationMs) {
        Timer.create(meterRegistry)
            .description("Module operation duration")
            .tag("operation", name)
            .publishPercentiles(0.5, 0.95, 0.99)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }
}
```

### Structured Logging
```java
MDC.put("correlationId", correlationId);
MDC.put("userId", userId);
log.info("Operation completed", 
    kv("operation", "export"), 
    kv("duration_ms", duration),
    kv("status", "success"));
```

### Correlation IDs
- All requests should have a unique correlation ID
- IDs should propagate across service boundaries
- Log every operation with the correlation ID

---

## 🔐 Security Checklist

Every module handling sensitive data must:

- [ ] Validate ALL inputs (not just type, but business logic)
- [ ] Check authorization before every operation
- [ ] Never log sensitive data (PII, tokens, API keys)
- [ ] Use encrypted connections (HTTPS/TLS)
- [ ] Implement rate limiting if exposed to internet
- [ ] Use parameterized queries (prevent SQL injection)
- [ ] Handle errors safely (no stack traces in responses)

---

## 📚 Documentation Requirements

### Module README.md
```markdown
# {Module Name}

## What does it do?
Brief description (1-2 paragraphs).

## Quick Start
How to run locally, basic usage example.

## Configuration
Environment variables needed.

## APIs
List of key endpoints.

## Testing
How to run tests.

## Troubleshooting
Common issues and fixes.
```

### Operational Runbook
Location: `primus-platform/docs/modules/{module}-ops.md`

```markdown
# Operational Guide: {Module}

## Health Checks
- Endpoint: GET /actuator/health

## Troubleshooting
## Common Issues
## Rollback Procedure
...
```

---

## 🔄 Dependency Management

When your module depends on another module:

1. **Check if dependency is complete**
   ```bash
   python tools/strategy_task_router.py deps
   ```

2. **Block if dependency isn't done**
   - Add error message to your task's blockers
   - Don't proceed until dependency is complete

3. **If you find a missing dependency**
   - Document it in your `RISKS` section
   - Mention it in your handoff notes
   - Create a follow-up task if needed

---

## 🤝 Code Review Checklist (for Reviewers)

- [ ] Entry criteria satisfied (depends_on modules are complete)
- [ ] Exit criteria checklist is 100% complete
- [ ] Tests pass locally
- [ ] Tests have negative-path coverage
- [ ] Observability (metrics/logging/correlation IDs) present
- [ ] Security controls in place (no hardcoded secrets, proper auth checks)
- [ ] Documentation is complete and accurate
- [ ] No breaking changes to shared contracts
- [ ] Performance is acceptable (no obvious N+1 queries, etc.)

---

## 🛠️ Common Issues & Solutions

### Issue: "Module not found in settings.gradle"
**Solution**: Add to `settings.gradle`:
```groovy
include ':primus-platform:primus-services:module-name'
```

### Issue: "Dependency on incomplete module"
**Solution**: Check `strategy.tasks.yaml` for the dependency's status. If not done, your task is still blocked.

### Issue: "Build fails with Spring Boot version conflict"
**Solution**: All modules should use the same Spring Boot version defined in parent `build.gradle`.

### Issue: "Correlation ID not propagating to downstream service"
**Solution**: Use Spring's `MDCTaskDecorator` or `MDCInitializingTaskExecutor` for async operations.

---

## 📞 Getting Help

1. **Check existing docs** in `primus-platform/docs/`
2. **Review the tier plan** for your module (e.g., `TIER_2_MS_PLAN_AND_TODO.md`)
3. **Look at similar modules** for patterns
4. **Check exit criteria** to see what's expected

---

## ✅ Before You Mark a Task Done

Run this checklist:

```bash
# 1. Build passes
./gradlew :primus-platform:primus-services:{your-module}:build

# 2. Tests pass
./gradlew :primus-platform:primus-services:{your-module}:test

# 3. Documentation is complete
ls -la primus-platform/docs/modules/{your-module}-*

# 4. Module is in settings.gradle
grep "your-module" settings.gradle

# 5. Observability is present
grep -r "meterRegistry\|log.info\|MDC" src/main/java
```

Once all ✅, update `strategy.status.example.json`:
```json
{
  "completed": ["T2-AUDIT", "T2-APPROVAL", ...],
  "in_progress": []
}
```

---

Thank you for contributing to Primus! 🎉

