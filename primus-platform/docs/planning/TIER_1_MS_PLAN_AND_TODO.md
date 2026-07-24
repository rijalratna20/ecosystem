# Tier 1 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 1 focuses on foundation and stability. The goal is to harden core Primus runtime services, close critical quality gaps, and prepare clean contracts for Tier 2+ modules.

## Tier 1 Outcomes (Target)
- Stable export and retrieval pipelines for production-like usage
- Consistent auth and authorization behavior across all APIs
- Verified metadata/YAML validation with predictable error handling
- Baseline performance numbers and release readiness checklist

## Microservices in Tier 1

### 1) `primus-server` (Core Orchestration API)
**Role**
- Entry point for registration, export requests, retrieval requests, and admin APIs

**Required Features**
- Application registration and metadata version management
- Request validation for export/retrieval endpoints
- Idempotency support for repeated requests
- Standardized error model (codes + actionable messages)
- Correlation ID propagation for traceability

**Tier 1 Deliverables**
- OpenAPI contract completion for Tier 1 endpoints
- Contract tests for request/response schemas
- Endpoint-level authorization checks

---

### 2) `primus-export` (Export Execution Service)
**Role**
- Executes export logic from metadata and annotations

**Required Features**
- Metadata-driven relationship traversal from root entity
- Sensitive-field masking hook integration
- Deterministic export packaging (JSON/XML)
- Retry with bounded attempts and error classification
- Export status model (requested/running/completed/failed)

**Tier 1 Deliverables**
- End-to-end integration test for at least one reference flow
- Failure-path handling for source/read/storage failures
- Baseline throughput and latency metrics collection

---

### 3) `primus-retrieval` (Retrieval and Assembly Service)
**Role**
- Retrieves stored transaction data and assembles complete response payloads

**Required Features**
- Retrieval by transaction identifier with access control checks
- Multi-artifact assembly (structured data + documents where available)
- Consistent response format and pagination for list/search responses
- Error-safe partial retrieval handling with clear diagnostics

**Tier 1 Deliverables**
- API contract and integration tests for retrieval scenarios
- Performance baseline for common retrieval sizes
- Response compatibility checks with `primus-ui`

---

### 4) `primus-authenticator` (Identity and Access Service)
**Role**
- Handles token validation and role mapping

**Required Features**
- JWT/OAuth2 token validation and expiry handling
- Role-based authorization mapping for Primus actions
- Deny-by-default policy for sensitive APIs
- Standard auth failure responses (401/403 semantics)

**Tier 1 Deliverables**
- Security test matrix for token/role combinations
- Hardened edge-case handling for expired/invalid tokens
- Shared auth middleware usage across core services

---

### 5) `primus-nas` (Storage Service - Current Backend)
**Role**
- Stores and retrieves export artifacts in standardized structure

**Required Features**
- Consistent folder/path conventions
- Safe write semantics (temporary write + atomic move where possible)
- Checksum validation for integrity
- Retry and backoff for transient I/O failures

**Tier 1 Deliverables**
- Storage/read integration tests for export/retrieval flow
- Standardized error mapping for I/O and not-found cases
- Readiness notes for future storage abstraction in Tier 3

---

### 6) `primus-ui` (User Interface, Tier 1 Compatibility Scope)
**Role**
- Consumes Tier 1 APIs for search, request, and retrieval workflows

**Required Features (Tier 1 scope)**
- Compatible API handling for validation and error states
- Stable screens for request status and retrieval result
- Minimal telemetry hooks (request ID display/log linkage)

**Tier 1 Deliverables**
- API compatibility verification with `primus-server` and `primus-retrieval`
- UX fixes for common validation/auth failure cases

## Cross-Service Requirement Features (Must-Have)
- **Contract-first APIs:** OpenAPI-aligned request/response contracts
- **Validation-first behavior:** reject invalid metadata/requests early
- **Security baseline:** consistent authN/authZ and sensitive-data controls
- **Resilience baseline:** retries, bounded timeouts, and classified failures
- **Observability baseline:** correlation IDs, service logs, key timing metrics

## Tier 1 TODO List

### A. Planning and Design
- [ ] Finalize Tier 1 service scope and ownership map
- [ ] Freeze Tier 1 API contracts for `primus-server`, `primus-export`, `primus-retrieval`
- [ ] Define common error schema and error code catalog
- [ ] Define minimum SLA targets (p95 latency, success rate)

### B. Core Service Delivery
- [ ] Implement/complete request validation in `primus-server`
- [ ] Implement export status lifecycle and retry bounds in `primus-export`
- [ ] Implement retrieval assembly and response consistency checks in `primus-retrieval`
- [ ] Harden token and role checks in `primus-authenticator`
- [ ] Add checksum + integrity verification path in `primus-nas`

### C. Quality and Verification
- [ ] Add unit tests for validation, auth, masking hooks, and retry logic
- [ ] Add integration tests: export -> store -> retrieve flow
- [ ] Add contract tests between `primus-ui` and backend APIs
- [ ] Run baseline load/performance checks and capture results

### D. Release Readiness
- [ ] Publish Tier 1 runbook (startup, failure modes, troubleshooting)
- [ ] Publish known limitations and deferred items to Tier 2+
- [ ] Define go/no-go checklist and acceptance sign-off

## Exit Criteria for Tier 1
- [ ] All Tier 1 APIs documented and contract-tested
- [ ] End-to-end export/retrieval path passes in CI
- [ ] No critical/high unresolved defects in auth, export, retrieval, storage paths
- [ ] Baseline metrics recorded and approved by platform owners

