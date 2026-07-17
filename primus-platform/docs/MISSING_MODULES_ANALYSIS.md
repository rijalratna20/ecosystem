# Enterprise Module Analysis & Recommendations

## Current Module Inventory

### ✅ Currently Implemented

**Annotations (primus-annotations/)**
- primus-export (Export annotations)
- primus-api (API contract annotations) 
- authenticator (Auth annotations)

**Libraries (primus-libs/)**
- primus-common (DTOs, exceptions, utils)
- primus-core (Contract parsing, transaction logic)
- primus-security (JWT, OAuth2, RBAC)
- primus-utils (String, date, collection utils)
- primus-client-sdk (SDK for applications)

**Services (primus-services/)**
- primus (Main service - registration, export/retrieval)
- primus-nas (NAS storage backend - SMB/NFS)
- shadow-test (Testing & validation)

**UI**
- primus-ui (Angular - main app)
- shadow-ui (Angular - experimental)

---

## MISSING Modules for Enterprise Production

### 🔴 **Tier 1: CRITICAL - Must Have** (v1.0)

#### 1. **primus-audit** (Service)
**Purpose**: Governance and compliance - immutable audit trail for every action

**Responsibilities**:
- Record every export initiated and completed
- Record every retrieval request and access
- Track who accessed what data, when, and why
- Support audit report generation
- Enable compliance audits (SOX, HIPAA, GDPR)
- Retention policies enforcement

**Why Critical**:
- Regulatory compliance (SOX, HIPAA, GDPR require audit trails)
- Must prove "who saw sensitive data and when"
- Production requirement, not optional

**Key APIs**:
```
POST   /api/v1/audit/log         - Record event (internal only)
GET    /api/v1/audit/exports     - Export audit trail
GET    /api/v1/audit/retrievals  - Retrieval audit trail
GET    /api/v1/audit/sensitive   - Who accessed sensitive fields
GET    /api/v1/audit/reports     - Generate compliance reports
```

---

#### 2. **primus-approval** (Service)
**Purpose**: Workflow for approving sensitive data exports

**Responsibilities**:
- Require approval for sensitive transactions (credit cards, SSN)
- Multi-level approval (dev lead → security → compliance)
- Approval tracking and history
- SLA enforcement (approve within 24 hours)
- Email notifications for pending approvals
- Rejection with reason tracking

**Why Critical**:
- Enterprises require approval for production data
- Prevents unauthorized data leakage
- Compliance requirement

**Key APIs**:
```
POST   /api/v1/approvals                    - Create approval request
GET    /api/v1/approvals/pending            - List pending approvals
POST   /api/v1/approvals/{id}/approve       - Approve transaction
POST   /api/v1/approvals/{id}/reject        - Reject with reason
GET    /api/v1/approvals/{id}/history       - Approval history
```

---

#### 3. **primus-storage-s3** (Service)
**Purpose**: Cloud storage backend (AWS S3, Azure Blob, GCP)

**Responsibilities**:
- Store exports in cloud object storage
- Multi-region replication
- Lifecycle policies (archive old data)
- Encryption key management
- Cost optimization
- Performance monitoring

**Why Critical**:
- NAS alone doesn't scale globally
- Enterprises need cloud backup/DR
- S3 is standard for cloud deployments

**Configuration**:
```yaml
storage:
  s3:
    enabled: true
    bucket: primus-exports
    region: us-east-1
    replication: multi-region
    lifecycle:
      transitionDays: 90
      deleteafter: 365
```

---

#### 4. **primus-storage-database** (Service)
**Purpose**: PostgreSQL, MySQL, Oracle backend for structured data

**Responsibilities**:
- Store metadata and export records in database
- Query large datasets efficiently (indexed)
- Transaction support
- ACID guarantees
- Connection pooling

**Why Critical**:
- For metadata registry (not production data)
- Scales better than file systems for queries
- Required for audit trail (high throughput writes)

---

#### 5. **primus-notification** (Service)
**Purpose**: Send notifications for exports, approvals, failures

**Responsibilities**:
- Email notifications (export ready, approval pending, failures)
- Slack/Teams integration for DevOps alerts
- SMS for critical exports
- Notification templates
- Delivery tracking

**Why Critical**:
- Users need to know when data is ready
- Operations need alerts for failures
- Dependency for approval workflow

**Templates**:
```
- ExportReady: "Your export [ID] is ready: download_link"
- ApprovalPending: "New sensitive export requires approval: link_to_approve"
- ExportFailed: "Export [ID] failed: reason"
- HighVolume: "Alert: 1000+ exports in last hour"
```

---

#### 6. **primus-api-gateway** (Service)
**Purpose**: Central API gateway with routing, rate limiting, auth

**Responsibilities**:
- Route requests to appropriate service
- Rate limiting (per user, per app, per IP)
- Request logging and metrics
- SSL/TLS termination
- API versioning support
- Request/response transformation

**Why Critical**:
- Essential for production deployments
- Required for security (rate limiting DDoS protection)
- Needed for management and monitoring

**Features**:
```yaml
gateway:
  rateLimit:
    defaultMaxRequests: 1000
    perMinute: true
  auth:
    jwt:
      enabled: true
      secret: ${JWT_SECRET}
  logging:
    level: INFO
    includeHeaders: [Authorization, X-Request-ID]
```

---

### 🟠 **Tier 2: HIGH PRIORITY** (v1.1)

#### 7. **primus-monitoring** (Service)
**Purpose**: System health, performance metrics, alerting

**Responsibilities**:
- Export pipeline performance monitoring
- Storage backend health checks
- Database connection pool monitoring
- Alert thresholds (SLA violations, high latency)
- Dashboard and visualization

**Key Metrics**:
- Export success rate
- Export duration (p50, p95, p99)
- Retrieval latency
- Storage backend response time
- Error rates by type
- Concurrent users

---

#### 8. **primus-config** (Service)
**Purpose**: Centralized configuration management

**Responsibilities**:
- Store configuration (not in code/env vars)
- Environment-specific configs
- Feature flags
- Hot reload (no restart needed)
- Version and audit configuration changes

**Why Important**:
- Separate config from code
- Enable feature flags (A/B testing, gradual rollout)
- Audit who changed what and when

---

#### 9. **primus-cli** (Library/Tool)
**Purpose**: Command-line interface for DevOps, admins, developers

**Responsibilities**:
- Register applications: `primus app register --file metadata.yaml`
- Export data: `primus export --app LoanService --date 2024-01-01`
- Query exports: `primus exports list --status COMPLETED`
- Approve exports: `primus approval approve --id app123`
- View audit logs: `primus audit show --app CustomerService`
- Manage storage: `primus storage cleanup --older-than 90d`

**Commands**:
```bash
primus app register --file app.yaml
primus export --app LoanService --filter "customerId=12345"
primus exports list --status COMPLETED --format json
primus approval approve --id req_123 --reason "Needed for UAT"
primus audit show --app CreditService --from 2024-01-01 --to 2024-01-31
```

---

#### 10. **primus-scheduler** (Service)
**Purpose**: Recurring exports, maintenance jobs, data cleanup

**Responsibilities**:
- Schedule recurring exports (daily, weekly)
- Automatic data cleanup (remove expired exports)
- Backup jobs
- Metadata sync tasks
- Vacuum and index optimization

**Examples**:
```yaml
schedules:
  - name: daily_customer_export
    app: CustomerService
    schedule: "0 2 * * *"  # 2 AM daily
    filter: "lastModified >= yesterday"
  
  - name: cleanup_old_exports
    schedule: "0 3 * * 0"  # 3 AM Sundays
    action: delete_expired
    olderThan: 90d
```

---

#### 11. **primus-data-lineage** (Service)
**Purpose**: Track data flow: source → export → usage → archive

**Responsibilities**:
- Record data source (prod database, API, file)
- Track transformations (masking, format conversion)
- Record retrieval: who got what data, when
- Archive tracking: where is the data now
- Support compliance investigations

**Queries**:
- "Show me all exports of customer 12345's SSN"
- "Who has accessed credit card data in the last 30 days?"
- "Where is this customer's data stored?"

---

#### 12. **primus-migration** (Service)
**Purpose**: Migrate applications from old export systems to Primus

**Responsibilities**:
- Map existing SQL queries to Primus metadata
- Validate new exports match old output
- Gradual migration (run both in parallel)
- Rollback capability
- Data reconciliation

---

### 🟡 **Tier 3: IMPORTANT** (v1.2)

#### 13. **primus-multi-tenancy** (Library Module)
**Purpose**: Support multiple isolated tenants (if SaaS model)

**Responsibilities**:
- Data isolation per tenant
- Per-tenant configuration
- Per-tenant rate limits and quotas
- Per-tenant audit logs
- Per-tenant storage backends

---

#### 14. **primus-cache** (Library Module)
**Purpose**: Advanced caching strategy for performance

**Responsibilities**:
- Cache frequently accessed exports (Redis)
- Cache metadata registry (in-memory + distributed)
- Cache sensitive field mappings
- Cache approval workflows
- Invalidation strategies

---

#### 15. **primus-sdk-python** (Library)
**Purpose**: Python SDK for Python applications

**Why**:
- Many data tools are Python (pandas, scripts)
- Enterprises need Python SDK alongside Java SDK

---

#### 16. **primus-sdk-typescript** (Library)
**Purpose**: TypeScript/JavaScript SDK for Node.js applications

---

#### 17. **primus-backup-recovery** (Service)
**Purpose**: Backup and disaster recovery

**Responsibilities**:
- Backup metadata registry
- Backup export data per policy
- PITR (Point-in-Time Recovery)
- Cross-region replication
- Recovery testing

---

#### 18. **primus-encryption** (Service)
**Purpose**: Encryption key management, encryption/decryption

**Responsibilities**:
- Key generation and rotation
- Integration with HSM (Hardware Security Module)
- Transparent field-level encryption
- Key audit trail
- BYOK (Bring Your Own Key) support

---

### 🟢 **Tier 4: NICE TO HAVE** (v2.0+)

#### 19. **primus-analytics** (Service)
Usage analytics, dashboards, trends:
- Most exported collections
- Peak export times
- Export size trends
- Application usage patterns

#### 20. **primus-recommendations** (Service)
Suggest metadata improvements:
- "This collection is export with Collection X 95% of the time"
- "Consider adding these fields to your search schema"
- "Field Y looks like sensitive data"

#### 21. **primus-self-service-portal** (UI)
Let applications manage their own metadata:
- Upload/edit metadata.yaml
- View export history
- Request approvals
- View audit logs

#### 22. **primus-openapi-generator** (Tool)
Generate OpenAPI/Swagger docs from metadata

#### 23. **primus-graphql** (Service)
GraphQL endpoint for flexible querying

#### 24. **primus-streaming** (Service)
Real-time data streaming (Kafka integration)

---

## Recommended Implementation Order

### **Phase 1 (v1.0): MVP for Enterprise** ⚡
1. **primus-audit** - Compliance requirement
2. **primus-approval** - Security requirement
3. **primus-storage-s3** - Cloud deployment need
4. **primus-api-gateway** - Production infrastructure
5. **primus-notification** - User experience

**Timeline**: 6-8 weeks
**Team**: 2-3 backend engineers

---

### **Phase 2 (v1.1): Production Hardening** 🔧
6. **primus-monitoring** - Operational visibility
7. **primus-scheduler** - Automation
8. **primus-config** - Configuration management
9. **primus-cli** - DevOps tooling
10. **primus-data-lineage** - Compliance investigation

**Timeline**: 4-6 weeks
**Team**: 1-2 backend engineers + DevOps

---

### **Phase 3 (v1.2): Advanced Features** 🚀
11. **primus-storage-database** - Metadata optimization
12. **primus-sdk-python** - Multi-language support
13. **primus-backup-recovery** - DR capability
14. **primus-encryption** - Key management

**Timeline**: 4-6 weeks
**Team**: 1-2 backend engineers

---

## Module Dependency Graph (Including Missing)

```
Applications
    │
    ├────────────────────┬──────────────────┐
    │                    │                  │
    ▼                    ▼                  ▼
primus-client-sdk    primus-cli        primus-ui
    │                    │                  │
    └────────┬───────────┴─────┬──────────┬─┘
             │                 │          │
    ┌────────▼──────────┐      │          │
    │  primus-core      │      │          │
    │  primus-security  │      │          │
    └────────┬──────────┘      │          │
             │                 │          │
    ┌────────▼─────────┐      │          │
    │  primus-export   │      │          │
    │  primus-retrieval│      │          │
    └────────┬──────────┘      │          │
             │                 │          │
    ┌────────┴──────┬──────────┴──────┬───┘
    │               │                 │
    ▼               ▼                 ▼
primus-server   primus-approval   primus-audit
    │               │                 │
    ├───────┬───────┴─────┬───────────┤
    │       │             │           │
    ▼       ▼             ▼           ▼
primus-nas  primus-s3  primus-postgres  primus-notification
              │               │
              ▼               ▼
         primus-config  primus-monitoring
              │
              ▼
         primus-scheduler
              │
              ▼
      primus-data-lineage

🎯 Critical Path: storage → approval → audit → api-gateway
```

---

## Resource & Timeline Estimate

| Phase | Modules | Duration | Team Size | Effort |
|-------|---------|----------|-----------|--------|
| **v1.0 (MVP)** | 5 critical | 6-8 weeks | 3 backend | 45 dev-weeks |
| **v1.1 (Prod)** | 5 hardening | 4-6 weeks | 2-3 | 25 dev-weeks |
| **v1.2 (Advanced)** | 4 advanced | 4-6 weeks | 2 | 20 dev-weeks |
| **v2.0 (Nice)** | 6 advanced | 8-10 weeks | 2 | 30 dev-weeks |

**Total**: ~120 dev-weeks of effort (3 engineers for ~12 months)

---

## Success Criteria

### v1.0 Complete When:
- ✅ Every export is audited (who, what, when, why)
- ✅ Sensitive exports require approval from security team
- ✅ Data can be stored in cloud (S3)
- ✅ API is behind gateway with rate limiting
- ✅ Approval notifications arrive in Slack/email

### v1.1 Complete When:
- ✅ Operations dashboard shows system health
- ✅ Recurring exports work automatically
- ✅ DevOps can manage system from CLI
- ✅ Can trace data lineage (for investigations)

### v1.2 Complete When:
- ✅ Python and TypeScript SDKs available
- ✅ Backup/disaster recovery tested
- ✅ End-to-end encryption for sensitive data

---

## Quick Checklist: What's Missing?

- ❌ **Audit & Compliance**: primus-audit
- ❌ **Approval Workflow**: primus-approval  
- ❌ **Cloud Storage**: primus-storage-s3
- ❌ **Notifications**: primus-notification
- ❌ **API Security**: primus-api-gateway
- ❌ **Operations**: primus-monitoring, primus-scheduler
- ❌ **Config Management**: primus-config
- ❌ **CLI Tools**: primus-cli
- ❌ **Data Lineage**: primus-data-lineage
- ❌ **Database Storage**: primus-storage-database
- ❌ **Encryption**: primus-encryption
- ❌ **Multi-Language SDKs**: primus-sdk-python, primus-sdk-typescript
- ❌ **Backup/DR**: primus-backup-recovery

**In scope of original README implementation**: 13 missing modules

---

## Reclassification by Platform Capability (compact layout)

Per recent architecture guidance, the missing items are best expressed as platform capabilities rather than immediately as independent Gradle modules. The table below maps each capability to where it should live initially (package inside `primus-server`) and when to extract it into a standalone module.

| Capability | Start as | Extract to module when... |
|------------|----------|---------------------------|
| Audit & Compliance | `primus-server.audit` (package) | Needs own scaling, retention, or separate DB → `primus-audit` |
| Approval Workflow | `primus-server.approval` | Needs separate UI/integration or approval SLA management → `primus-approval` |
| Data Lineage | `primus-server.lineage` | Needs heavy graph/analytics or separate storage → `primus-data-lineage` |
| Notification | `primus-server.notification` | Multiple consumers or required reliability isolation → `primus-notification` |
| Config Management | `primus-server.configuration` | Needs feature flags or multi-environment UI → `primus-config` |
| Monitoring | `primus-server.monitoring` | Heavy metrics ingestion or separate alerting → `primus-monitoring` |
| Scheduler | `primus-server.scheduler` | High-volume job scheduling or separate tenancy → `primus-scheduler` |
| API Gateway | Separate infra component (Envoy/Kong) | Deploy as independent gateway service → `primus-api-gateway` |
| Storage Providers | Storage SPI + adapters in `primus-server.storage` + `primus-nas` | If you want independent deployment or multi-region object storage → `primus-storage-s3`, `primus-storage-database` |
| Metadata Engine | Standalone `primus-metadata` (recommended) | Keep standalone from day one — central core of platform |
| Plugin Framework | `primus-plugin` (library) | Keep as library — plugins installed into `primus-server` runtime |
| Search/Indexing | `primus-search` (standalone or hosted) | If indexing load increases, run as separate service backed by ES/OpenSearch |
| Transform Pipelines | `primus-server.transforms` (package) or `primus-transform` (lib) | Extract when reusable across services or for performance isolation |

This approach keeps the repo and Gradle graph compact while retaining a clear path to extraction when a capability requires independent scaling, separate operational ownership, or reuse by multiple services.

