# Tier 9 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 9 focuses on ecosystem adoption through SDKs and integrations. The goal is to make Primus accessible to non-Java teams and embed it into common enterprise toolchains.

## Tier 9 Outcomes (Target)
- Stable external API contract baseline for SDK consumption
- Initial SDKs published with examples and tests
- Integration adapters for workflow and collaboration tools
- Compatibility policy for SDK/server versioning

## Microservices and Artifacts in Tier 9

### 1) `primus-sdk-python` (New SDK)
**Role**
- Python client for export/retrieval/audit workflows

**Required Features**
- Auth-enabled client initialization
- Typed request/response models
- Sync and async usage patterns
- Error mapping aligned with server contracts

### 2) `primus-sdk-go` and `primus-sdk-node` (New SDKs)
**Role**
- Additional language access paths based on adoption needs

**Required Features**
- Core API coverage parity with Python SDK
- Versioned packages and semantic versioning discipline
- Example projects and integration tests

### 3) `primus-integrations` (New Adapter Set)
**Role**
- Connect Primus with external platforms

**Required Features**
- Event/webhook adapters
- Workflow pipeline integration hooks
- Auth and secret handling guidance per integration

## Tier 9 TODO List

### A. API Contract Stabilization
- [ ] Freeze SDK-facing API surface and versioning policy
- [ ] Publish changelog and deprecation rules
- [ ] Add compatibility matrix between server and SDK versions

### B. SDK Delivery
- [ ] Implement and publish `primus-sdk-python`
- [ ] Implement and publish `primus-sdk-go`
- [ ] Implement and publish `primus-sdk-node`
- [ ] Add examples, quick starts, and CI test suites

### C. Integration Delivery
- [ ] Build `primus-integrations` reference adapters
- [ ] Add webhook/event-driven usage examples
- [ ] Add enterprise authentication integration notes

### D. Quality and Lifecycle
- [ ] Add contract tests shared across SDKs
- [ ] Add SDK release automation and security scanning
- [ ] Add support policy for bugfix and minor/major releases

## Exit Criteria for Tier 9
- [ ] At least one production-ready SDK published and tested
- [ ] Additional SDKs available with parity for core workflows
- [ ] Integration adapters validated in representative scenarios
- [ ] Compatibility/versioning policy enforced in release process

