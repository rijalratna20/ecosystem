# Primus Incomplete Gradle Module Strategy

## Scope and Intent
This file breaks down incomplete Primus microservice/module work into executable Gradle module tasks with explicit entry/exit criteria.

It is designed for:
- GitHub Copilot coding agent workflows
- Claude Code coding agent workflows
- Human-led planning and delivery tracking

## Source of Truth Used
- `settings.gradle`
- `primus-platform/docs/MISSING_MODULES_ANALYSIS.md`
- `primus-platform/docs/planning/TIER_1_10_PLAN_AND_TODO.md`
- `primus-platform/docs/planning/TIER_2_MS_PLAN_AND_TODO.md` ... `TIER_10_MS_PLAN_AND_TODO.md`

## Current Baseline vs Gap
- Current topology is compact; several cross-cutting concerns still live as packages under `primus-server`.
- Missing work is mostly governance, storage abstraction, edge security, observability, operations, and ecosystem SDKs.
- Strategy here assumes extraction into dedicated Gradle modules where independent lifecycle is beneficial.

---

## Program Gates (Global)

### Entry gate for module extraction
A module can start only when all are true:
- Tier 1 stability baseline is met (core tests, contract tests, and major defects resolved).
- Module domain boundaries are documented (API, events, persistence ownership).
- Dependency contracts with `primus-server` and shared libs are defined.

### Exit gate for any module
A module is complete only when all are true:
- Gradle module builds in CI and is included in `settings.gradle`.
- API/event contracts are documented and contract-tested.
- Unit + integration tests pass, with negative-path coverage.
- Metrics/logging/security controls are implemented for the module.
- Runbook and operational notes are added under `primus-platform/docs/`.

---

## Incomplete Module Backlog (By Tier)

## Tier 2: Governance

### `primus-audit`
- Proposed path: `primus-platform/primus-services/primus-audit`
- Depends on: Tier 1 baseline, event schema definitions
- Entry criteria:
  - `AuditEvent` schema finalized (actor/action/target/outcome/timestamp/correlationId)
  - Append-only persistence design approved
- Exit criteria:
  - Ingest + query/report APIs implemented
  - End-to-end export/retrieval/admin audit capture validated
  - Retention/archive job verified

### `primus-approval`
- Proposed path: `primus-platform/primus-services/primus-approval`
- Depends on: `primus-audit`, policy model
- Entry criteria:
  - Approval state machine agreed (`PENDING/APPROVED/REJECTED/EXPIRED`)
  - Sensitive data classification tags available
- Exit criteria:
  - Approval APIs and policy evaluator working
  - SLA/escalation behavior verified
  - Sensitive flows blocked without valid approval

### `primus-server` (governance integration scope)
- Proposed path: existing `primus-platform/primus-services/primus`
- Depends on: `primus-audit`, `primus-approval`
- Entry criteria:
  - Governance APIs available in test environment
- Exit criteria:
  - Approval gate in orchestration path
  - Correlated governance events emitted for all decision points

---

## Tier 3: Storage Abstraction and Multi-Cloud

### `primus-storage-core`
- Proposed path: `primus-platform/primus-services/primus-storage-core`
- Depends on: Tier 1 baseline
- Entry criteria:
  - SPI contract drafted (put/get/list/delete + capability flags)
- Exit criteria:
  - SPI published and consumed by export/retrieval paths
  - Direct NAS-only coupling removed

### `primus-storage-s3`
- Proposed path: `primus-platform/primus-services/primus-storage-s3`
- Depends on: `primus-storage-core`
- Entry criteria:
  - Storage URI/policy contract finalized
- Exit criteria:
  - Multipart upload + encryption support validated
  - Integration and fallback tests pass

### `primus-storage-database`
- Proposed path: `primus-platform/primus-services/primus-storage-database`
- Depends on: `primus-storage-core`
- Entry criteria:
  - DB schema and indexing strategy approved
- Exit criteria:
  - Metadata/export record persistence operational
  - Throughput and query-performance targets met

### `primus-nas` (refactor scope)
- Proposed path: existing `primus-platform/primus-services/primus-nas`
- Depends on: `primus-storage-core`
- Entry criteria:
  - SPI compatibility test contract available
- Exit criteria:
  - NAS adapter passes SPI contract tests and resilience tests

---

## Tier 4: Notifications and UX

### `primus-notification`
- Proposed path: `primus-platform/primus-services/primus-notification`
- Depends on: event schema and user preference model
- Entry criteria:
  - Notification event types and templates approved
- Exit criteria:
  - Email/webhook delivery with retries + DLQ works
  - Delivery tracking APIs exposed

### `primus-ui` (notification/UX scope)
- Proposed path: existing UI module
- Depends on: `primus-notification`
- Entry criteria:
  - API contracts for preferences/history frozen
- Exit criteria:
  - Preference screens, saved filters, improved status UX delivered

---

## Tier 5: API Protection and Scale

### `primus-api-gateway`
- Proposed path: `primus-platform/primus-services/primus-api-gateway`
- Depends on: auth strategy + routing model
- Entry criteria:
  - Gateway policy model finalized (rate limit, payload rules, TLS/security headers)
- Exit criteria:
  - External traffic routed through gateway
  - Rate/payload constraints enforced and tested
  - Gateway audit-friendly logs + alerts available

---

## Tier 6: Observability

### `primus-monitoring`
- Proposed path: `primus-platform/primus-services/primus-monitoring`
- Depends on: telemetry standards
- Entry criteria:
  - Metric/log/trace taxonomy approved
- Exit criteria:
  - Dashboards + alerts operational
  - Core services emit standard telemetry with correlation IDs

---

## Tier 7: Config and Deployment

### `primus-config`
- Proposed path: `primus-platform/primus-services/primus-config`
- Depends on: secret-management decision
- Entry criteria:
  - Config hierarchy, rollback, and access model defined
- Exit criteria:
  - Centralized config/feature flags consumed by services
  - Config changes auditable and access-controlled

### `primus-provisioning`
- Proposed path: `primus-platform/primus-services/primus-provisioning`
- Depends on: environment target matrix (dev/test/prod)
- Entry criteria:
  - IaC/deployment artifact format selected
- Exit criteria:
  - Repeatable provisioning/deployment templates shipped
  - Rollback procedures validated

---

## Tier 8: Operations Tooling

### `primus-cli`
- Proposed path: `primus-platform/primus-libs/primus-cli` (or `primus-services/primus-cli`)
- Depends on: stable API contracts
- Entry criteria:
  - CLI command catalog and output formats approved
- Exit criteria:
  - Core operator commands implemented with stable exit codes
  - Automation-friendly JSON output validated

### `primus-scheduler`
- Proposed path: `primus-platform/primus-services/primus-scheduler`
- Depends on: job model + notification integration
- Entry criteria:
  - Job state model and retry policies approved
- Exit criteria:
  - Recurring jobs execute reliably with controls/observability
  - Failure recovery runbook validated

### `primus-data-lineage`
- Proposed path: `primus-platform/primus-services/primus-data-lineage`
- Depends on: export/retrieval event contracts
- Entry criteria:
  - Lineage graph/event schema finalized
- Exit criteria:
  - Source->export->retrieval traceability queryable
  - Impact analysis views/API tested

---

## Tier 9: SDK and Integrations

### `primus-sdk-python`
- Proposed path: `primus-platform/primus-libs/primus-sdk-python`
- Depends on: frozen SDK-facing API surface
- Entry criteria:
  - Versioning and compatibility policy approved
- Exit criteria:
  - Published package with examples and integration tests

### `primus-sdk-node` (or `primus-sdk-typescript`)
- Proposed path: `primus-platform/primus-libs/primus-sdk-node`
- Depends on: Python SDK baseline patterns
- Entry criteria:
  - Core parity scope agreed
- Exit criteria:
  - Core workflow parity achieved and tested

### `primus-sdk-go`
- Proposed path: `primus-platform/primus-libs/primus-sdk-go`
- Depends on: contract stability
- Entry criteria:
  - Go SDK MVP scope approved
- Exit criteria:
  - Core workflows supported with contract tests

### `primus-integrations`
- Proposed path: `primus-platform/primus-services/primus-integrations`
- Depends on: event/webhook standards
- Entry criteria:
  - Target integrations prioritized
- Exit criteria:
  - Reference adapters validated in representative scenarios

---

## Tier 10: Enterprise Hardening Workstreams

### `primus-dr-ha`
- Proposed path: `primus-platform/primus-services/primus-dr-ha` (or program docs only)
- Entry criteria:
  - Multi-region architecture decisions finalized
- Exit criteria:
  - Repeatable failover tests meet RTO/RPO targets

### `primus-compliance`
- Proposed path: `primus-platform/primus-services/primus-compliance` (or program docs only)
- Entry criteria:
  - Control mapping framework approved
- Exit criteria:
  - Evidence export/reporting audit-ready

### `primus-cost-optimization`
- Proposed path: `primus-platform/primus-services/primus-cost-optimization` (or program docs only)
- Entry criteria:
  - Cost attribution model defined
- Exit criteria:
  - Cost reduction loop and alerting produce measurable outcomes

---

## Agent-Consumable Task Pack

Use this canonical YAML shape when assigning module tasks to Copilot or Claude Code agents.

```yaml
version: 1
project: primus-platform
global_entry:
  - Tier 1 stability baseline complete
  - Module boundaries and contracts documented
global_exit:
  - Gradle module builds in CI
  - API/event contracts tested
  - Unit/integration tests passing
  - Security/observability/runbook complete
tasks:
  - id: T2-AUDIT
    module: primus-audit
    tier: 2
    path: primus-platform/primus-services/primus-audit
    depends_on: [tier1-baseline]
    entry_criteria:
      - AuditEvent schema approved
      - Append-only persistence design approved
    exit_criteria:
      - Ingest/query/report APIs implemented
      - End-to-end governance capture validated
      - Retention/archive job validated
```

## Ready-to-Use Agent Prompts

### Prompt Template for GitHub Copilot Agent
```text
You are implementing module: <MODULE_NAME> in <MODULE_PATH>.
Follow strategy.MD global gates and this module's entry/exit criteria exactly.

Tasks:
1) Scaffold/update Gradle module and include it in settings/build aggregation.
2) Implement APIs/events/persistence per module criteria.
3) Add unit + integration tests for happy path and negative path.
4) Add observability (metrics/logging/correlation IDs) and security checks.
5) Update docs/runbook under primus-platform/docs.

Definition of done:
- All module exit criteria in strategy.MD are satisfied.
- Build and tests pass for impacted modules.
- Provide a short change log and residual risks.
```

### Prompt Template for Claude Code Agent
```text
Implement <MODULE_NAME> using the module plan in strategy.MD.
Treat strategy.MD as the contract for scope, entry criteria, and exit criteria.

Execution requirements:
- Keep changes limited to relevant modules and shared contracts.
- Prefer incremental commits by subtask: scaffold -> domain -> APIs -> tests -> docs.
- Validate with Gradle tests for touched modules.
- If a dependency module is missing, create a minimal stub and document the follow-up.

Output format:
1) Files changed
2) Criteria satisfied (entry/exit checklist)
3) Test results
4) Open risks/blockers
```

---

## Suggested Assignment Order
1. Tier 2 (`primus-audit`, `primus-approval`, governance integration)
2. Tier 3 (`primus-storage-core`, `primus-storage-s3`, `primus-nas` refactor)
3. Tier 4 + Tier 5 (`primus-notification`, `primus-api-gateway`)
4. Tier 6 + Tier 7 (`primus-monitoring`, `primus-config`, `primus-provisioning`)
5. Tier 8+ (`primus-cli`, `primus-scheduler`, `primus-data-lineage`, SDK/integration modules)

## Notes
- Keep extraction pragmatic: if a capability remains small and tightly coupled, a package under `primus-server` may be acceptable temporarily.
- Promote to standalone Gradle module when lifecycle, scaling, or ownership diverges.

