# Primus Platform - Overview

## What is Primus?

**Primus is a secure, metadata-driven data replication platform** that enables production-like data to be safely exported, stored, and retrieved for lower environments (DEV, QA, UAT, Performance, Training) without requiring custom code for each application.

Instead of every application building its own scripts, SQL exports, masking logic, storage conventions, and UI, Primus provides a **standardized platform** where applications describe what data they own through YAML metadata, and Primus handles how it is exported, secured, stored, searched, and retrieved.

## Why Primus Exists

Organizations with many microservices face critical challenges with production data management:

### Problems Today

1. **Production data cannot safely be copied** - Contains SSN, credit cards, addresses, DOB, banking info - simply copying is a security risk
2. **Every application exports data differently** - Custom scripts (mongxport, exportCustomer.sh, GET /export) - nothing standardized
3. **No common metadata** - Knowledge about collections, relationships, formats lives in developers' heads
4. **Difficult to reconstruct transactions** - Customer → Accounts → Applications → Documents → Audit trail - no way to know what belongs together
5. **Sensitive fields are inconsistent** - One app masks SSN, another forgets; one masks DOB, another exposes it
6. **Lower environments become stale** - Data arrives days late due to manual processes
7. **UI is hardcoded** - Search pages, result grids, detail pages, filters built repeatedly for each app
8. **No governance** - No centralized way to track what was exported, by whom, when, and why

Primus solves these through **metadata-driven automation and centralized governance**.

## What Primus Solves

| Problem | Primus Solution |
|---------|-----------------|
| Production data copy security | Centralized masking & authorization before export |
| Every app exports differently | Standardized @PrimusExport annotation + YAML metadata |
| No common metadata | Central metadata registry with versioning |
| Complex transactions spread across collections | Transaction-aware export collecting related collections |
| Inconsistent sensitive field handling | Centralized sensitive-field.yaml with role-based masking |
| Slow data availability | Automated fast export pipeline with parallel processing |
| Repeated UI development | Auto-generate search, grids, filters, detail pages |
| No governance/audit trail | Centralized audit log for every export and retrieval |

## Platform Concepts

### Contract
A YAML definition describing:
- **Application Metadata**: What data the application owns
- **Collections**: Related entities (Customer, Account, Audit)
- **Relationships**: How entities connect (Customer → Accounts → Applications)
- **Search Fields**: Which fields are searchable (email, customerId)
- **Sensitive Fields**: Which fields require masking (SSN, creditCard, DOB)
- **Storage**: Where to store (NAS, S3, PostgreSQL)
- **Retention**: How long to keep (90 days, 1 year)

### Transaction
A complete business transaction containing all related collections:
- **Root Entity**: Customer (ID: 12345)
- **Related Collections**: Customer data, Accounts, Applications, Documents, Audit logs
- **State**: Point-in-time snapshot of the transaction
- **Metadata**: Export timestamp, export ID, version, checksum

### Application
A registered consumer of the Primus platform:
- Has **registered metadata** (collections, relationships, sensitive fields)
- **Requests exports** with filters (Customer ID, date range)
- **Retrieves previously exported** transactions
- **Gets custom UI** generated from its metadata
- **All actions audited** and governed

### Export Pipeline
The process of getting production data securely:
1. Parse application request (transaction ID, date range)
2. Validate against registered metadata
3. Query source systems for all related collections
4. Apply masking rules based on requester role
5. Serialize to requested format (JSON, XML, CSV)
6. Compress and upload to storage backend
7. Calculate checksum and record audit trail
8. Return transaction ID and access credentials

### Retrieval Pipeline
The process of consuming exported data safely:
1. Authenticate user/application
2. Verify permissions for data access
3. Locate export in storage (NAS/S3/DB)
4. Deserialize from storage format
5. Apply field-level masking per requester role
6. Validate data integrity (checksum)
7. Stream or paginate results
8. Record audit trail
9. Return data to requester

### Metadata Registry
Central repository of truth:
- What data each application owns
- How collections relate to each other
- Which fields are sensitive
- Retention policies
- Version history
- Audit logs

## Terminology

| Term | Definition |
|------|-----------|
| **Application** | A microservice or system that owns data and requests exports/retrievals |
| **Collection** | A logical grouping of related entities (e.g., Customer data, Account data) |
| **Metadata** | Descriptive information about application, collections, relationships, sensitive fields |
| **Sensitive Field** | Data requiring encryption and role-based masking (SSN, credit card, DOB) |
| **Transaction** | Complete business event with all related collections (Customer + Accounts + Audit) |
| **Export** | Process of extracting production data with masking and storing securely |
| **Retrieval** | Process of fetching previously exported data with authorization and masking |
| **Audit Trail** | Immutable log of every export, retrieval, and access to sensitive data |
| **Governance** | Centralized control of metadata, versioning, permissions, and compliance |
| **Query Schema** | YAML defining which fields are searchable and how to filter |
| **Sensitive Field Schema** | YAML mapping fields to masking rules per role |
| **Metadata Registry** | Central database of all applications and their metadata versions |

## High-Level Architecture

```
Applications                    Primus Platform                   Storage
┌──────────────┐               ┌────────────────────┐            ┌──────────┐
│ Web App      │──Register───→ │  Registration Svc  │            │   NAS    │
│ Mobile App   │               │                    │            ├──────────┤
│ Third Party  │  ←──Export─── │  Export Engine     │            │    S3    │
│              │               │                    │            ├──────────┤
└──────────────┘  ←──Retrieve─ │  Retrieval Engine  │            │   DB     │
                                │                    │            └──────────┘
                  JWT/OAuth     │  Auth Service      │
                  ───────────→  │                    │
                  ←───Token───  └────────────────────┘
                                        ↓
                                  Metadata Cache
```

## Platform Layers

### 1. Annotation Layer (primus-annotations)
What developers write:
- `@PrimusExport` on classes/methods to mark for export
- `@PrivateField` on fields that need masking (SSN, credit card)
- `@QuerySchema` to define searchable fields
- `@SensitiveSchema` to define masking rules per role

### 2. Library Layer (primus-libs)
Core engines and utilities:
- **primus-common**: Shared DTOs, exceptions, utilities
- **primus-export**: Export engine (query, validate, transform, store)
- **primus-retrieval**: Retrieval engine (search, authenticate, mask, stream)
- **primus-security**: JWT, OAuth2, RBAC, field-level authorization
- **primus-core**: Contract parsing, transaction assembly
- **primus-client-sdk**: SDK for applications to call Primus

### 3. Service Layer (primus-services)
Microservices implementing business logic:
- **primus (main)**: Registration, metadata registry, export/retrieval coordination
- **primus-nas**: NAS storage backend (SMB/NFS integration)
- **primus-s3** (MISSING): Cloud storage (AWS S3, Azure Blob) 
- **primus-postgres** (MISSING): Database storage backend
- **primus-audit** (MISSING): Governance and audit trail service
- **primus-approval** (MISSING): Approval workflow engine

### 4. Presentation Layer
User interfaces:
- **primus-ui (Angular)**: Main web application (generated components)
- **primus-cli** (MISSING): Command-line for DevOps teams
- **primus-api-gateway** (MISSING): API gateway with rate limiting, routing

## Key Features

### For Developers
✅ **Metadata-First**: Describe data once with YAML metadata, annotations handle everything  
✅ **Standardized Integration**: Use `@PrimusExport` and `@PrimusRetrieval` instead of custom code  
✅ **No Export Logic**: Primus handles querying, masking, compression, storage  
✅ **Auto-Generated APIs**: REST endpoints created from annotations  
✅ **Auto-Generated UI**: Search, grids, filters, detail pages created from metadata  

### For Operations
✅ **Consistent Storage Structure**: All exports follow standard folder layout  
✅ **Centralized Governance**: All metadata, versions, permissions in one place  
✅ **Version Control**: Track metadata changes across time  
✅ **Audit Everything**: Every export, retrieval, and access logged  
✅ **Easy Backups**: Standardized storage makes backup policies simpler  

### For Security
✅ **No Production DB Access**: Developers never access prod directly  
✅ **Centralized Masking**: Sensitive fields masked consistently per role  
✅ **JWT Authentication**: Secure token-based access  
✅ **Role-Based Authorization**: RBAC built into every transaction  
✅ **Encryption**: In-transit & at-rest encryption with key management  
✅ **Audit Trail**: Immutable log of every action (export, retrieval, access)  

### For Enterprise
✅ **Standardization**: All applications export/retrieve the same way  
✅ **Faster Onboarding**: New apps need only metadata + annotations  
✅ **Reduced Duplication**: 6-month dev work becomes 2 files of YAML  
✅ **Consistent UX**: Every application has the same search/detail interface  
✅ **Compliance Ready**: Centralized governance, versioning, audit trail for regulations  

## Next Steps

→ [Architecture Overview](./01-architecture.md) - Deployment & component diagrams  
→ [Gradle Modules](./02-gradle-modules.md) - Complete module dependency table  
→ [Data Contracts](./03-contracts.md) - Contract examples and spec  
→ [Module Reference](./modules/) - Detailed module documentation

## Recommended Compact Architecture

To keep the codebase manageable during early development, prefer a compact Gradle layout and implement cross-cutting capabilities as packages inside `primus-server` until reuse or independent lifecycle requires extraction.

Suggested top-level modules (compact):

```
primus-parent

primus-common
primus-contract
primus-annotations
primus-sdk
primus-auth-client
primus-metadata

primus-server        # orchestration: audit, approval, notification, monitoring, scheduler, transforms
primus-nas           # storage adapter (filesystem/NAS)
primus-search        # indexing/search (ES/OpenSearch shim)

primus-ui
shadow-server
shadow-ui
```

Key capabilities that should initially live as packages inside `primus-server` (extract later if needed):

- `audit` (immutable audit log, reports)
- `approval` (multi-level workflows)
- `notification` (email, Slack, Teams)
- `monitoring` (metrics, health)
- `scheduler` (recurring jobs, cleanup)
- `configuration` (centralized runtime config)
- `transforms` (transformation pipelines and adapters)

Core platform additions:

- `primus-metadata` — central metadata engine (YAML parsing, validation, versioning, JSON/UI generation)
- `primus-plugin` — plugin framework (masking providers, exporters, validators)
- `primus-transform` (or `primus-transforms`) — transformation pipeline engine and config
- `Storage SPI` — pluggable storage provider interface (NAS, S3, DB, Azure, GCS)
- `Event Bus` — publish/subscribe backbone (Kafka/Rabbit/Redis Streams) for lifecycle events

Why this layout?

- Keeps the Gradle project graph small and easy to build during early development
- Encourages clear separation between SDKs (clients) and platform services
- Lets operations and governance capabilities evolve inside `primus-server` and be promoted to standalone services only when necessary

Example extraction path: start with `primus-server` packages → when a package is reused or needs separate deployment, extract it into `primus-audit`, `primus-approval`, `primus-notification`, etc.

