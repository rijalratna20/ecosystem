# Tier 8 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 8 focuses on advanced operations tooling. The goal is to reduce manual operations effort and improve platform control-plane capabilities.

## Tier 8 Outcomes (Target)
- CLI available for core operator workflows
- Scheduler handles recurring export jobs
- Data lineage available for export/retrieval traceability
- Recovery tooling for stuck and failed jobs

## Microservices in Tier 8

### 1) `primus-cli` (New Operator Tool)
**Role**
- Command-line interface for platform operations

**Required Features**
- Commands for export, retrieval, audit queries, and health checks
- Auth integration and secure profile handling
- Machine-readable output for automation pipelines

### 2) `primus-scheduler` (New Job Service)
**Role**
- Manages recurring and delayed execution workflows

**Required Features**
- Cron-based scheduling and timezone handling
- Job state machine and retry policies
- Concurrency limits and queue controls

### 3) `primus-data-lineage` (New Traceability Service)
**Role**
- Tracks provenance and downstream usage of exported data

**Required Features**
- Source-to-export and export-to-retrieval relationships
- Query APIs for lineage lookup
- Impact analysis views for schema or policy changes

## Tier 8 TODO List

### A. CLI Delivery
- [ ] Define CLI command set and output formats
- [ ] Implement auth/session profile handling
- [ ] Implement core commands for export/retrieve/audit/health
- [ ] Add script-friendly flags and exit codes

### B. Scheduler Delivery
- [ ] Implement schedule CRUD APIs and execution engine
- [ ] Implement retries, backoff, and failure classification
- [ ] Add queue visibility and operational controls
- [ ] Integrate notifications for job results

### C. Lineage Delivery
- [ ] Define lineage event model and persistence schema
- [ ] Implement lineage capture from export/retrieval workflows
- [ ] Implement lineage query APIs and UI-ready responses
- [ ] Add impact analysis reports for governance teams

### D. Integration and Validation
- [ ] End-to-end tests for scheduled export lifecycle
- [ ] Operational tests for retries and stuck job recovery
- [ ] Data consistency tests for lineage graph accuracy

## Exit Criteria for Tier 8
- [ ] Operators can run core workflows through CLI
- [ ] Recurring jobs execute reliably with observability and controls
- [ ] Lineage data is queryable and useful for audits and impact analysis
- [ ] Runbooks exist for scheduler and lineage operations

