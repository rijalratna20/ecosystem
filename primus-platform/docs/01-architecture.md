# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Applications Layer                               │
│  ┌──────────┐  ┌──────────────┐  ┌─────────────┐  ┌────────────────┐     │
│  │ Web App  │  │ Mobile App   │  │ Third Party │  │ Internal Tools │     │
│  └────┬─────┘  └──────┬───────┘  └──────┬──────┘  └────────┬───────┘     │
└───────┼──────────────────┼────────────────┼─────────────────┼──────────────┘
        │                  │                │                 │
        └──────────────────┼────────────────┼─────────────────┘
                           │
                           ▼
        ┌──────────────────────────────────────────────┐
        │     API Gateway / Load Balancer (SSL/TLS)    │
        │  (Authentication, Rate Limiting, Routing)   │
        └──────────────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
   ┌─────────┐        ┌─────────┐       ┌──────────┐
   │ Primus  │        │ Primus  │       │ Shadow   │
   │ Server  │        │  NAS    │       │  Test    │
   │         │        │ Service │       │ Service  │
   └────┬────┘        └────┬────┘       └─────┬────┘
        │                  │                   │
        ├──────────────────┼───────────────────┤
        │                  │                   │
        ▼                  ▼                   ▼
    ┌───────────────────────────────────────┐
    │   Shared Libraries (primus-libs)      │
    │  - primus-common                      │
    │  - primus-export                      │
    │  - primus-retrieval                   │
    │  - primus-security                    │
    │  - primus-core                        │
    └──────────────┬────────────────────────┘
                   │
        ┌──────────┼──────────────┐
        │          │              │
        ▼          ▼              ▼
    ┌────────┐ ┌────────┐   ┌─────────────┐
    │  NAS   │ │   S3   │   │ PostgreSQL  │
    │Storage │ │Storage │   │   Storage   │
    └────────┘ └────────┘   └─────────────┘
        │          │              │
        └──────────┼──────────────┘
                   │
                   ▼
          ┌──────────────────┐
          │  Metadata Cache  │
          │  (Redis/Memory)  │
          └──────────────────┘
        
        ┌─────────────────────────┐
        │ UI Layer                │
        │ ┌─────────────────────┐ │
        │ │  primus-angular     │ │
        │ │  (Generated + Core) │ │
        │ └─────────────────────┘ │
        │ ┌─────────────────────┐ │
        │ │  shadow-angular     │ │
        │ │  (Experimental)     │ │
        │ └─────────────────────┘ │
        └─────────────────────────┘
```

## Deployment Architecture

```
                    ┌──────────────────────┐
                    │   CDN / Static Files │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │ Load Balancer (DNS)  │
                    │ (SSL/TLS Termination)│
                    └──────────┬───────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
        ┌───────▼─────┐ ┌─────▼──────┐ ┌────▼──────┐
        │ Kubernetes  │ │ Kubernetes │ │ Kubernetes│
        │  Node 1     │ │  Node 2    │ │  Node 3   │
        │             │ │            │ │           │
        │ ┌─────────┐ │ │ ┌────────┐ │ │ ┌───────┐ │
        │ │ Primus  │ │ │ │ Primus │ │ │ │Primus │ │
        │ │ Server  │ │ │ │ Server │ │ │ │Server │ │
        │ │(Pod)    │ │ │ │(Pod)   │ │ │ │(Pod) │ │
        │ └────┬────┘ │ │ └───┬────┘ │ │ └──┬───┘ │
        │      │      │ │     │      │ │    │     │
        │ ┌────▼────┐ │ │ ┌───▼────┐ │ │ ┌──▼──┐ │
        │ │ NAS Svc │ │ │ │NAS Svc │ │ │ │NAS  │ │
        │ │ (Pod)   │ │ │ │(Pod)   │ │ │ │Svc  │ │
        │ └────┬────┘ │ │ └───┬────┘ │ │ └──┬──┘ │
        └──────┼──────┘ └─────┼──────┘ └────┼────┘
               │              │             │
               └──────────────┼─────────────┘
                              │
                    ┌─────────▼──────────┐
                    │ Shared Storage     │
                    │ ┌────────────────┐ │
                    │ │ PostgreSQL DB  │ │
                    │ ├────────────────┤ │
                    │ │ Redis Cache    │ │
                    │ ├────────────────┤ │
                    │ │ NAS (NFS Mount)│ │
                    │ ├────────────────┤ │
                    │ │ S3 Bucket      │ │
                    │ └────────────────┘ │
                    └────────────────────┘
```

## Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Primus Server                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐       ┌───────────────────────────┐  │
│  │ REST Controllers │       │ Admin Controllers         │  │
│  ├──────────────────┤       ├───────────────────────────┤  │
│  │ /register        │       │ /admin/applications       │  │
│  │ /export          │       │ /admin/contracts          │  │
│  │ /retrieve        │       │ /admin/audit              │  │
│  └────────┬─────────┘       └────────────┬──────────────┘  │
│           │                              │                │
│           └──────────────┬───────────────┘                │
│                          │                                │
│  ┌──────────────────────▼────────────────────┐           │
│  │        Service Layer                      │           │
│  ├───────────────────────────────────────────┤           │
│  │ ContractService  │  RegistrationService  │           │
│  │ ExportService    │  RetrievalService     │           │
│  │ MetadataService  │  AuditService         │           │
│  └────────┬─────────────────────────────────┘           │
│           │                                             │
│  ┌────────▼─────────────────────────────────┐           │
│  │  primus-libs (Shared Libraries)          │           │
│  ├───────────────────────────────────────────┤           │
│  │ Export Engine    │ primus-common         │           │
│  │ Retrieval Engine │ primus-security       │           │
│  │ Validators       │ primus-core           │           │
│  └────────┬─────────────────────────────────┘           │
│           │                                             │
└───────────┼─────────────────────────────────────────────┘
            │
    ┌───────┴────────┐
    │                │
    ▼                ▼
Metadata Cache    Storage Backends
(Redis)           (NAS/S3/DB)
```

## Data Flow - Export Operation

```
Application
    │
    │ POST /api/register (metadata.yaml)
    ▼
┌─────────────────────────────────┐
│  Registration Service           │
│  - Parse YAML contract          │
│  - Validate schema              │
│  - Create application entry     │
└────────────┬────────────────────┘
             │
             │ Approve/Store
             ▼
┌─────────────────────────────────┐
│  Contract Storage (Metadata DB) │
└────────────┬────────────────────┘
             │
             │ POST /api/export (query.yaml)
             ▼
┌─────────────────────────────────┐
│  Export Engine                  │
│  - Parse query contract         │
│  - Validate permissions         │
│  - Query source data            │
│  - Apply sensitive masking      │
│  - Serialize to format          │
└────────────┬────────────────────┘
             │
             │ Upload to storage
             ▼
┌─────────────────────────────────┐
│  Storage Backend                │
│  - Store file (NAS/S3/DB)       │
│  - Calculate checksum           │
│  - Store metadata               │
└────────────┬────────────────────┘
             │
             │ Return access info
             ▼
Application (with transaction ID)
```

## Data Flow - Retrieval Operation

```
Application
    │
    │ GET /api/retrieve/{transactionId}
    ▼
┌──────────────────────────────────┐
│  Metadata Cache (Redis)          │
│  - Check if in cache             │
└────┬──────────────────────────────┘
     │ Cache hit? (metadata only)
     ├─────────────────────────┐
     │                         │
     │ (Yes - return metadata) │ (No - query DB)
     └→ Cache Miss             ▼
                        ┌──────────────────┐
                        │  Metadata DB     │
                        │  - Lookup export │
                        │  - Check perms   │
                        └────────┬─────────┘
                                 │
                                 ▼
                        ┌──────────────────┐
                        │ Apply Masking    │
                        │ - Hide sensitive │
                        │ - Filter fields  │
                        └────────┬─────────┘
                                 │
                                 ▼
                        ┌──────────────────┐
                        │ Retrieve Engine  │
                        │ - Query storage  │
                        │ - Deserialize   │
                        │ - Stream data    │
                        └────────┬─────────┘
                                 │
                                 │ Paginated/Streamed
                                 ▼
                            Application
```

## Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.x, Spring MVC |
| **Build** | Gradle 8.x with multi-module |
| **Contract Format** | YAML |
| **Data Formats** | JSON, CSV, Parquet, XML |
| **Serialization** | Jackson, Avro |
| **Storage** | PostgreSQL 13+, S3/Object Storage, NAS (SMB/NFS) |
| **Caching** | Redis, In-Memory (Caffeine) |
| **Authentication** | JWT, OAuth2, Spring Security |
| **Security** | AES-256 encryption, HTTPS/TLS 1.3 |
| **Frontend** | Angular 16+, TypeScript, RxJS |
| **UI Framework** | Angular Material, Bootstrap |
| **Logging** | SLF4J, Logback, ELK Stack |
| **Metrics** | Micrometer, Prometheus |
| **Containerization** | Docker, Kubernetes |
| **API Docs** | OpenAPI/Swagger |

## Module Dependency Graph

```
Applications
    │
    ├────────────────────┬──────────────────┐
    │                    │                  │
    ▼                    ▼                  ▼
primus-client-sdk   primus-ui        Generated SDKs
    │                    │
    └────────┬───────────┴─────┬──────────────┐
             │                 │              │
             ▼                 ▼              ▼
      primus-core      primus-security   primus-common
             │                 │              │
      ┌──────┴─────────┬───────┴──────┐     │
      │                │              │      │
      ▼                ▼              ▼      │
 primus-export   primus-retrieval   primus-utils
      │                │              │
      └────────────────┼──────────────┘
                       │
            ┌──────────┴──────────┐
            │                     │
            ▼                     ▼
      primus-server        primus-nas

External Dependencies
      │
      ├── Spring Boot
      ├── Jackson
      ├── Lombok
      ├── Log4j
      ├── JWT (jjwt)
      ├── Guava
      └── Junit5 / Mockito
```

## Security Architecture

```
┌──────────────────────────────────────┐
│      API Gateway / Firewall          │
│   (Rate Limiting, DDOS Protection)   │
└────────────────┬─────────────────────┘
                 │
         ┌───────▼────────┐
         │ TLS/SSL Layer  │
         │ (Port 443)     │
         └───────┬────────┘
                 │
         ┌───────▼──────────────────┐
         │ Auth Filter (Spring Sec) │
         │ - Extract JWT            │
         │ - Validate Signature     │
         │ - Check Expiration       │
         └───────┬──────────────────┘
                 │
         ┌───────▼────────────────────┐
         │ Authorization Interceptor  │
         │ - RBAC Check              │
         │ - Field-level Masking     │
         │ - Permissions Validation  │
         └───────┬────────────────────┘
                 │
         ┌───────▼────────────────┐
         │ Encryption Layer       │
         │ - Sensitive Fields     │
         │ - Data at Rest         │
         │ - Data in Transit      │
         └───────┬────────────────┘
                 │
                 ▼
         Protected Resources
```

## Request Lifecycle

```
1. Client Request
   │
   ├─ Authorization Header (JWT/API Key)
   ├─ Application ID
   ├─ Request Payload
   │
   ▼
2. API Gateway
   ├─ SSL/TLS Termination
   ├─ Rate Limiting
   ├─ Request Validation
   │
   ▼
3. Authentication Filter
   ├─ Extract Token
   ├─ Validate Signature
   ├─ Check Expiration
   ├─ Extract User/App Info
   │
   ▼
4. Authorization Check
   ├─ RBAC Validation
   ├─ Resource Permission Check
   ├─ Field-Level Permissions
   │
   ▼
5. Business Logic
   ├─ Contract Validation
   ├─ Data Processing
   ├─ Storage Operations
   │
   ▼
6. Response Generation
   ├─ Serialize Data
   ├─ Apply Masking
   ├─ Compression
   │
   ▼
7. Send Response
   └─ HTTPS Response
```

## Scaling Strategy

### Horizontal Scaling
- **Stateless Services**: All instances identical
- **Load Balancing**: Round-robin or least-connections
- **Auto-scaling**: Based on CPU/memory metrics
- **Database Connection Pooling**: Shared connection pool

### Vertical Scaling
- **JVM Tuning**: Heap size, GC optimization
- **Database Optimization**: Indexing, caching strategies
- **Storage I/O**: Parallel reads, compression

### Caching Strategy
- **Metadata Cache** (Redis): Contract definitions,permissions
- **Data Cache** (Redis): Frequently accessed exports
- **CDN Cache**: Static UI assets
- **Browser Cache**: Client-side caching headers

## High Availability

```
┌────────────────────────────────────────────────┐
│         Active-Active Deployment               │
├────────────────────────────────────────────────┤
│  Region 1               Region 2               │
│  ┌──────────────────┐  ┌──────────────────┐   │
│  │ Primus Cluster   │  │ Primus Cluster   │   │
│  │ (3+ nodes)       │  │ (3+ nodes)       │   │
│  └────────┬─────────┘  └────────┬─────────┘   │
│           │                     │             │
│           └──────────┬──────────┘             │
│                      │                       │
│           ┌──────────▼──────────┐            │
│           │ Distributed Cache   │            │
│           │ (Redis Sentinel)    │            │
│           └──────────┬──────────┘            │
│                      │                       │
│           ┌──────────▼──────────┐            │
│           │  Multi-Master DB    │            │
│           │ (Replication)       │            │
│           └─────────────────────┘            │
└────────────────────────────────────────────────┘
```

Next → [Gradle Modules](./02-gradle-modules.md)

