# Tier 2 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 2 focuses on governance. The goal is to implement auditability and approval workflows so sensitive export/retrieval operations are traceable, reviewable, and policy-controlled.

## Tier 2 Outcomes (Target)
- Immutable audit trail across export, retrieval, and admin actions
- Approval workflow for sensitive operations with SLA and escalation
- Governance APIs and UI views for compliance teams
- Security-aligned event logging with least-data exposure

## Microservices in Tier 2

### 1) `primus-audit` (New Governance Service)
**Role**
- Central system of record for governance events and compliance queries

**Required Features**
- Append-only audit event store
- Event model with actor, action, target, outcome, timestamp, correlation ID
- Query APIs (time range, user, application, action, status)
- Compliance report endpoints (CSV/JSON export)
- Retention and archival policy support

**Tier 2 Deliverables**
- Audit event schema v1 and persistence model
- Core APIs for ingest and search/report
- Export/retrieval event ingestion hooks from core services

---

### 2) `primus-approval` (New Workflow Service)
**Role**
- Enforces policy-driven approvals before sensitive exports/retrievals

**Required Features**
- Approval request lifecycle (`PENDING`, `APPROVED`, `REJECTED`, `EXPIRED`)
- Policy rules by app, data class, and user role
- Multi-step approval support (single or multi-approver)
- SLA timers and escalation path
- Decision comments and full history tracking

**Tier 2 Deliverables**
- Approval APIs and state machine implementation
- Policy evaluator for approval requirements
- Escalation scheduler and timeout handling

---

### 3) `primus-server` (Orchestration Integration)
**Role**
- Integrates governance checks into request flow

**Required Features**
- Pre-execution approval gate for sensitive requests
- Audit emission for request acceptance/rejection
- Correlation ID consistency between request and governance events
- Admin endpoints for policy assignment references

**Tier 2 Deliverables**
- Approval-gated export/retrieval orchestration path
- Unified governance error responses for blocked requests

---

### 4) `primus-export` (Governance-Aware Export)
**Role**
- Emits audit events and respects approval decisions

**Required Features**
- Emit lifecycle events: requested, started, completed, failed
- Capture masking policy metadata used during execution
- Block execution when approval is missing/expired
- Include governance context in failure details

**Tier 2 Deliverables**
- Governance event hooks for all export stages
- Strict approval validation before execution

---

### 5) `primus-retrieval` (Governance-Aware Retrieval)
**Role**
- Emits access events and applies approval controls for protected retrievals

**Required Features**
- Audit events for retrieval access attempts and outcomes
- Approval gate for protected data classes
- Traceable reason codes for denied access

**Tier 2 Deliverables**
- Retrieval access event model integration
- Policy-aligned decision path for protected payloads

---

### 6) `primus-ui` (Governance UX Scope)
**Role**
- Provides operator and approver interfaces for governance workflows

**Required Features**
- Pending approvals queue and decision actions
- Approval history and status timeline
- Audit search and filter screen
- Export/retrieval views with governance status badges

**Tier 2 Deliverables**
- Governance dashboards for approvers and compliance users
- Error/decision UX for approval-required actions

## Tier 2 Requirement Features (Must-Have)
- **Immutability-first:** audit events must not be editable in normal flow
- **Policy-first execution:** sensitive operations require policy evaluation
- **Least-privilege visibility:** show only permitted audit fields to each role
- **Deterministic decisions:** approval outcomes are explicit and reproducible
- **Compliance-ready exports:** report APIs support external review workflows

## Tier 2 TODO List

### A. Architecture and Contracts
- [ ] Finalize Tier 2 domain models (`AuditEvent`, `ApprovalRequest`, `ApprovalPolicy`)
- [ ] Define OpenAPI contracts for `primus-audit` and `primus-approval`
- [ ] Define governance error catalog (`APPROVAL_REQUIRED`, `POLICY_DENIED`, `APPROVAL_EXPIRED`)
- [ ] Define data classification tags consumed by policy engine

### B. `primus-audit` Delivery
- [ ] Implement append-only persistence and ingestion API
- [ ] Implement search/query APIs with pagination and filtering
- [ ] Implement report export endpoints for compliance reviews
- [ ] Implement retention/archive job and retention policy settings
- [ ] Add masking for sensitive fields in audit payload output

### C. `primus-approval` Delivery
- [ ] Implement approval request creation and lifecycle transitions
- [ ] Implement policy evaluator and rule matching
- [ ] Implement approver assignment and re-assignment flows
- [ ] Implement SLA timers and escalation notifications/events
- [ ] Implement approval history trail and rationale capture

### D. Core Service Integration
- [ ] Integrate approval gate in `primus-server` request orchestration
- [ ] Integrate audit event emission in `primus-server`, `primus-export`, `primus-retrieval`
- [ ] Add idempotency behavior for duplicate approval requests
- [ ] Add correlation ID propagation across governance events

### E. UI and Access Controls
- [ ] Build pending approvals page in `primus-ui`
- [ ] Build audit search/report page in `primus-ui`
- [ ] Add RBAC controls for approver/compliance/admin roles
- [ ] Add governance status indicators in request/result screens

### F. Testing and Verification
- [ ] Unit tests for policy evaluator and approval state machine
- [ ] Integration tests for approval-gated export/retrieval
- [ ] Negative tests for unauthorized audit access
- [ ] Performance tests for high-volume audit ingestion
- [ ] Data retention tests for policy enforcement

### G. Release Readiness
- [ ] Publish Tier 2 governance runbook (operations + incident handling)
- [ ] Publish compliance mapping notes (how reports map to controls)
- [ ] Define go/no-go checklist with platform and security owners

## Exit Criteria for Tier 2
- [ ] Sensitive flows are blocked unless valid approval exists
- [ ] End-to-end audit trail is captured for export/retrieval/admin actions
- [ ] Governance APIs are documented and contract-tested
- [ ] RBAC is enforced for governance views and actions
- [ ] Retention and archival jobs run successfully in validation environment

