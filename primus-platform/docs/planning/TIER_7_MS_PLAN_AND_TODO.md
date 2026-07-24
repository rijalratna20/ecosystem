# Tier 7 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 7 focuses on configuration governance and deployment automation. The goal is to make service configuration consistent, secure, and environment-portable.

## Tier 7 Outcomes (Target)
- Centralized configuration model with environment overrides
- Secret management integrated for sensitive settings
- Feature flags available for controlled rollouts
- Repeatable infrastructure and deployment templates

## Microservices in Tier 7

### 1) `primus-config` (New Configuration Service)
**Role**
- Central source for runtime configuration and feature flags

**Required Features**
- Hierarchical config resolution
- Runtime config versioning and rollback
- Feature flag evaluation API
- Access control and audit on config changes

### 2) `primus-provisioning` (New Deployment/IaC Module)
**Role**
- Delivers repeatable environment provisioning artifacts

**Required Features**
- Environment templates for dev/test/prod
- Service deployment manifests/charts
- Dependency provisioning recipes (db/cache/storage)

### 3) Core Services (Config Consumer Scope)
**Role**
- Consume central config and feature flags safely

**Required Features**
- Config bootstrap and refresh behavior
- Safe defaults for missing values
- Config change visibility in logs/metrics

## Tier 7 TODO List

### A. Config Platform
- [ ] Define config hierarchy and override policy
- [ ] Implement `primus-config` APIs and persistence model
- [ ] Add feature flag model and evaluation endpoints
- [ ] Add change audit and approval policy for sensitive config

### B. Secret and Security Integration
- [ ] Integrate secret provider for credentials/keys
- [ ] Enforce encrypted storage and transport for config secrets
- [ ] Add access controls for read/write scopes

### C. Provisioning and Deployment
- [ ] Build `primus-provisioning` environment templates
- [ ] Add deploy manifests/charts for all active services
- [ ] Add migration scripts for config schema changes

### D. Validation and Operations
- [ ] Test config rollout and rollback scenarios
- [ ] Test feature flag safe rollout patterns
- [ ] Publish deployment and rollback runbooks

## Exit Criteria for Tier 7
- [ ] Services consume centralized config reliably
- [ ] Feature flags support controlled release decisions
- [ ] Infrastructure/deployment artifacts are repeatable across environments
- [ ] Configuration security and audit controls are in place

