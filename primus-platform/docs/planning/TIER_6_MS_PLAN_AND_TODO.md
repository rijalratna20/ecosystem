# Tier 6 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 6 focuses on observability. The goal is to provide clear metrics, logs, and traces across core workflows to reduce detection and resolution time.

## Tier 6 Outcomes (Target)
- Common telemetry standards implemented across services
- Dashboards for performance, reliability, and governance flows
- Actionable alerting with runbooks
- Distributed traces for export and retrieval critical paths

## Microservices in Tier 6

### 1) `primus-monitoring` (New Observability Integration)
**Role**
- Provides shared telemetry conventions and dashboard definitions

**Required Features**
- Metrics naming and tagging conventions
- Dashboard templates for API/export/retrieval/storage
- Alert rule definitions and thresholds

### 2) Core Services (`primus-server`, `primus-export`, `primus-retrieval`, `primus-audit`, `primus-approval`)
**Role**
- Emit standardized telemetry

**Required Features**
- Structured logs with correlation IDs
- Service metrics (latency, error rate, throughput)
- Trace spans for major operations

## Tier 6 TODO List

### A. Telemetry Standards
- [ ] Define metric catalog and label strategy
- [ ] Define structured logging schema
- [ ] Define tracing span taxonomy

### B. Service Instrumentation
- [ ] Add metrics instrumentation to core APIs
- [ ] Add structured logs to key decision points
- [ ] Add distributed tracing hooks across service boundaries

### C. Alerting and Dashboards
- [ ] Build dashboard set for API, export, retrieval, storage, governance
- [ ] Configure alerts for latency, error spikes, and SLA risks
- [ ] Link alerts to remediation playbooks

### D. Validation
- [ ] Test telemetry completeness for end-to-end workflows
- [ ] Validate trace continuity with correlation IDs
- [ ] Run incident simulation drills using dashboards and alerts

## Exit Criteria for Tier 6
- [ ] Core flows expose usable metrics/logs/traces
- [ ] Alert coverage exists for key reliability and SLA risks
- [ ] Operators can triage common incidents from observability tooling

