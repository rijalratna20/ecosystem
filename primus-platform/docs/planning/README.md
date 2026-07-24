# Primus Platform – Implementation Planning

> **Source**: Derived from [MISSING_MODULES_ANALYSIS.md](../MISSING_MODULES_ANALYSIS.md)  
> **Purpose**: Step-by-step implementation checklist for the 10 missing enterprise modules

---

## Overview

The MISSING_MODULES_ANALYSIS.md identified that the Primus platform is ~50% complete.
This planning document tracks the implementation of the 10 highest-priority missing modules,
ordered by the **Recommended Implementation Order** in the analysis.

---

## Implementation Tiers

### 🔴 Tier 1: CRITICAL (Phase 1 / v1.0 MVP)
Must be implemented before any production release.

| # | Module | Status | Gradle Subproject |
|---|--------|--------|-------------------|
| 1 | **primus-audit** | ✅ Implemented | `:primus-platform:primus-audit` |
| 2 | **primus-approval** | ✅ Implemented | `:primus-platform:primus-approval` |
| 3 | **primus-storage-s3** | ✅ Implemented | `:primus-platform:primus-storage-s3` |
| 4 | **primus-api-gateway** | ✅ Implemented | `:primus-platform:primus-api-gateway` |
| 5 | **primus-notification** | ✅ Implemented | `:primus-platform:primus-notification` |

### 🟠 Tier 2: HIGH PRIORITY (Phase 2 / v1.1)
Implement for production hardening and operations visibility.

| # | Module | Status | Gradle Subproject |
|---|--------|--------|-------------------|
| 6  | **primus-monitoring** | ✅ Implemented | `:primus-platform:primus-monitoring` |
| 7  | **primus-scheduler** | ✅ Implemented | `:primus-platform:primus-scheduler` |
| 8  | **primus-config** | ✅ Implemented | `:primus-platform:primus-config` |
| 9  | **primus-cli** | ✅ Implemented | `:primus-platform:primus-cli` |
| 10 | **primus-data-lineage** | ✅ Implemented | `:primus-platform:primus-data-lineage` |

---

## Module Details

### 1. primus-audit
**Purpose**: Immutable audit trail for every action in the platform.  
**Why Critical**: SOX, HIPAA, GDPR require proving "who saw sensitive data and when."  
**Key APIs**:
- `AuditService.record(AuditEvent)` – append-only event recording
- `AuditService.queryByApplication(appId, from, to)` – query by application
- `AuditService.querySensitiveAccess(from, to)` – HIPAA/GDPR investigation
- `AuditService.generateComplianceReport(from, to)` – compliance reports

**Package**: `com.primus.audit`  
**Location**: `primus-platform/primus-audit/`

---

### 2. primus-approval
**Purpose**: Multi-level approval workflow for sensitive data exports.  
**Why Critical**: Prevents unauthorized data leakage; enterprise compliance requirement.  
**Key APIs**:
- `ApprovalService.createRequest(ApprovalRequest)` – initiate approval
- `ApprovalService.approve(id, reviewer, note)` – approve request
- `ApprovalService.reject(id, reviewer, note)` – reject with reason
- `ApprovalService.expireOverdueRequests()` – SLA enforcement (called by scheduler)

**Package**: `com.primus.approval`  
**Location**: `primus-platform/primus-approval/`

---

### 3. primus-storage-s3
**Purpose**: Cloud object-storage backend (AWS S3, Azure Blob, GCP GCS).  
**Why Critical**: NAS alone doesn't scale globally; enterprises need cloud backup/DR.  
**Key APIs**:
- `S3StorageProvider.upload(key, data, length)` – store export data
- `S3StorageProvider.download(key)` – retrieve export data
- `S3StorageProvider.presignedUrl(key, expirySeconds)` – temporary download access
- `S3StorageConfig` – bucket, region, lifecycle, replication configuration

**Package**: `com.primus.storage.s3`  
**Location**: `primus-platform/primus-storage-s3/`

---

### 4. primus-api-gateway
**Purpose**: Central API gateway with routing, rate-limiting, and auth.  
**Why Critical**: Essential for production security (DDoS protection, JWT enforcement).  
**Key APIs**:
- `GatewayFilter.evaluate(path, principal, token)` – evaluate request
- `RouteRegistry.register(RouteDefinition)` – register upstream routes
- `RateLimiter.isAllowed(key)` – sliding-window rate limiting

**Package**: `com.primus.gateway`  
**Location**: `primus-platform/primus-api-gateway/`

---

### 5. primus-notification
**Purpose**: Email, Slack, and SMS notifications for exports, approvals, and failures.  
**Why Critical**: Users need to know when data is ready; ops needs failure alerts.  
**Key APIs**:
- `NotificationService.send(Notification)` – immediate delivery
- `NotificationService.enqueue(Notification)` – async delivery with tracking
- `NotificationTemplates` – factory for standard templates (ExportReady, ApprovalPending, etc.)

**Package**: `com.primus.notification`  
**Location**: `primus-platform/primus-notification/`

---

### 6. primus-monitoring
**Purpose**: System health, performance metrics, and SLA alerting.  
**Why Important**: Operations need visibility; SLA violations must be detected automatically.  
**Key APIs**:
- `HealthIndicator.check()` – SPI for per-component health
- `HealthAggregator.checkAll()` – platform-wide health summary
- `MetricRegistry` – counter/timer registry for export/retrieval metrics

**Package**: `com.primus.monitoring`  
**Location**: `primus-platform/primus-monitoring/`

---

### 7. primus-scheduler
**Purpose**: Recurring exports, maintenance jobs, and data cleanup.  
**Why Important**: Automates daily exports and prevents storage bloat.  
**Key APIs**:
- `JobScheduler.register(ScheduledJob)` – schedule a cron job
- `JobScheduler.triggerNow(id)` – manual trigger
- `JobScheduler.disable(id)` / `enable(id)` – lifecycle management

**Package**: `com.primus.scheduler`  
**Location**: `primus-platform/primus-scheduler/`

**Example cron jobs**:
```
daily_customer_export  → "0 2 * * *"   (2 AM daily)
cleanup_old_exports    → "0 3 * * 0"   (3 AM Sundays, delete >90 days old)
```

---

### 8. primus-config
**Purpose**: Centralized configuration management and feature flags.  
**Why Important**: Separates config from code; enables runtime feature toggles.  
**Key APIs**:
- `ConfigService.set(key, value, env, by, desc)` – store property
- `ConfigService.get(key, env)` – retrieve property
- `ConfigService.registerFlag(name, default, desc)` – create feature flag
- `ConfigService.enableFlag(name, by)` / `disableFlag(name, by)` – toggle flags

**Package**: `com.primus.config`  
**Location**: `primus-platform/primus-config/`

---

### 9. primus-cli
**Purpose**: Command-line interface for DevOps, admins, and developers.  
**Why Important**: Enables scriptable automation and admin workflows.  
**Commands**:
```bash
primus app register --file app.yaml
primus app list
primus export run --app LoanService --date 2024-01-01
primus exports list --status COMPLETED --format json
```
**Package**: `com.primus.cli`  
**Location**: `primus-platform/primus-cli/`

---

### 10. primus-data-lineage
**Purpose**: Track data flow from source through export, retrieval, and archive.  
**Why Important**: Required for compliance investigations ("where is customer 12345's data?").  
**Key APIs**:
- `LineageService.recordNode(LineageNode)` – record a data flow node
- `LineageService.recordEdge(LineageEdge)` – record a connection
- `LineageService.findDownstreamEdges(nodeId)` – forward traversal
- `LineageService.searchByAttribute(key, value)` – find data by subject

**Package**: `com.primus.lineage`  
**Location**: `primus-platform/primus-data-lineage/`

---

## Success Criteria

### Tier 1 Complete (v1.0) When:
- ✅ Every export is audited (who, what, when, why)
- ✅ Sensitive exports require approval from security team
- ✅ Data can be stored in cloud (S3 compatible)
- ✅ API is behind gateway with rate limiting
- ✅ Notifications sent for export events and approval requests

### Tier 2 Complete (v1.1) When:
- ✅ Operations dashboard shows system health
- ✅ Recurring exports work automatically
- ✅ DevOps can manage system from CLI
- ✅ Can trace data lineage for compliance investigations
- ✅ Feature flags allow gradual rollout

---

## Next Steps (Tier 3 & 4)

After completing Tiers 1–2, the following modules are recommended:

| Priority | Module | Tier |
|----------|--------|------|
| 11 | primus-storage-database | Tier 3 |
| 12 | primus-backup-recovery | Tier 3 |
| 13 | primus-sdk-python | Tier 3 |
| 14 | primus-sdk-typescript | Tier 3 |
| 15 | primus-encryption | Tier 3 |
| 16+ | primus-analytics, primus-graphql, etc. | Tier 4 |

See [MISSING_MODULES_ANALYSIS.md](../MISSING_MODULES_ANALYSIS.md) for full details.

---

**Last Updated**: July 2026  
**Platform Version**: v1.0 (Tiers 1–2 complete)
