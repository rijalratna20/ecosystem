# Primus Platform: Metadata-Driven Enterprise Data Replication

> A secure, metadata-driven platform for standardized data export, storage, search, and retrieval across microservices.

> **Status**: 50% production-ready (13/26 modules). See [roadmap](#roadmap--tier-structure) for Phase 1 critical work.

---

## 🎯 What is Primus?

Primus solves a critical enterprise problem: **How do you safely get production data to lower environments (DEV, QA, UAT) without building custom export scripts for every application?**

Instead of each microservice team writing their own:
- Export logic
- Masking rules
- Storage conventions
- Search UIs
- Retrieval APIs

...teams provide **metadata** (`metadata.yaml`, `query.yaml`, `sensitiveField.yaml`) and Primus handles everything else.

### The Problem Primus Solves

```
❌ Today:
  Customer Service  → exportCustomer.sh              (SQL dump)
  Loan Service      → mongoexport + masking script   (adhoc)
  Payment Service   → GET /export                    (REST endpoint)
  Fraud Service     → Shell script + SFTP            (manual)
  ...100+ teams     → Each solving it differently    (chaos)

✅ With Primus:
  ANY Service       → metadata.yaml + @PrimusExport  (consistent)
                      Primus handles: security, storage, search, UI, audit
```

### Key Benefits

| Aspect | Before | After |
|--------|--------|-------|
| **Time to integrate** | 2-4 weeks per app | 2-4 days per app |
| **Export/retrieval** | Custom per team | Standard platform API |
| **Data security** | Ad-hoc masking | Centralized policy engine |
| **User experience** | Built per app | Generated from metadata |
| **Audit trail** | None | Complete governance |
| **Storage backend** | Hardcoded NAS | Pluggable (NAS, S3, DB) |

---

## 📦 Project Structure

```
ai-sytem-design/
├── strategy.MD                          # Strategic roadmap (entry/exit criteria by tier)
├── strategy.tasks.yaml                  # Agent-consumable task backlog
├── strategy.status.example.json         # Status tracking template
├── requirements.txt                     # Python dependencies
├── tools/
│   ├── strategy_task_router.py         # Task dispatch tool for agents
│   ├── test_strategy_task_router.py    # Unit tests
│   └── README.md                        # Usage guide
├── primus-platform/                     # Main Gradle project root
│   ├── build.gradle
│   ├── settings.gradle                  # Module definitions
│   ├── README.MD                        # Primus platform overview
│   ├── EXECUTIVE_SUMMARY.md             # Analysis & recommendations
│   ├── MISSING_MODULES_ANALYSIS.md      # Detailed gap analysis
│   ├── PRIMUS_ARCHITECTURE.md           # Architecture deep-dive
│   ├── docs/
│   │   ├── 00-overview.md               # Architecture overview
│   │   ├── 01-architecture.md
│   │   ├── 02-gradle-modules.md
│   │   ├── ... (10+ docs)
│   │   ├── planning/
│   │   │   ├── TIER_1_10_PLAN_AND_TODO.md    # Overall roadmap
│   │   │   ├── TIER_2_MS_PLAN_AND_TODO.md    # Governance (audit, approval)
│   │   │   ├── TIER_3_MS_PLAN_AND_TODO.md    # Storage abstraction
│   │   │   ├── ... (Tiers 4-10)
│   ├── primus-annotations/              # Annotation framework
│   ├── primus-libs/                     # Shared libraries
│   └── primus-services/                 # Microservices (core + planned)
│       ├── primus/                      # Main orchestration service
│       ├── primus-nas/                  # NAS storage backend
│       ├── primus-audit/                # 🔴 PLANNED (Tier 2)
│       ├── primus-approval/             # 🔴 PLANNED (Tier 2)
│       ├── primus-storage-s3/           # 🔴 PLANNED (Tier 3)
│       └── ... (more planned services)
```

---

## 🏗️ Current Architecture vs. Enterprise Target

### Current (v0.1 - 50% complete)
- ✅ 13 modules implemented
- ✅ Core export/retrieval flows
- ✅ Metadata-driven annotation framework
- ✅ JWT authentication
- ✅ Dynamic UI generation (Angular)
- ✅ NAS storage backend
- ❌ **NO audit trail** (compliance blocker)
- ❌ **NO approval workflows** (security blocker)
- ❌ **NO cloud storage** (scale blocker)
- ❌ **NO notifications** (UX blocker)
- ❌ **NO rate limiting/gateway** (production blocker)

### Enterprise Target (v1.0)
- ✅ All of current, PLUS:
- 🔴 **primus-audit** (compliance)
- 🔴 **primus-approval** (security)
- 🔴 **primus-storage-s3** (multi-cloud)
- 🔴 **primus-notification** (user experience)
- 🔴 **primus-api-gateway** (edge security)
- 🟠 + 8 high-priority hardening modules
- 🟡 + more optimization/advanced features

---

## 🚀 Roadmap & Tier Structure

This project uses a **10-tier delivery model** to progressively harden from MVP (DEV/QA-only) to enterprise production.

### Phase 1: Critical for Production (v1.0 - 8 weeks, 5 modules)
| Tier | Module | Status | Purpose |
|------|--------|--------|---------|
| **T2** | **primus-audit** | 🔴 PLANNED | Immutable compliance audit trail |
| **T2** | **primus-approval** | 🔴 PLANNED | Workflow gates for sensitive exports |
| **T3** | **primus-storage-s3** | 🔴 PLANNED | Cloud storage + multi-region |
| **T4** | **primus-notification** | 🔴 PLANNED | Email/webhook delivery |
| **T5** | **primus-api-gateway** | 🔴 PLANNED | Rate limiting + request validation |

**Target completion**: 8 weeks | **Team**: 3 backend engineers

### Phase 2: Hardening Modules (v1.1 - 6 weeks, 5 modules)
- `primus-monitoring` (observability)
- `primus-config` (centralized config + feature flags)
- `primus-scheduler` (recurring exports)
- `primus-cli` (operator tooling)
- `primus-data-lineage` (compliance traceability)

### Phase 3: Advanced Features (v1.2 - ongoing)
- Multi-language SDKs (Python, Node, Go)
- Advanced integrations (Kafka streaming, GraphQL)
- Multi-tenancy support
- Disaster recovery & high availability

For detailed tier breakdown, see [strategy.MD](./strategy.MD) and `primus-platform/docs/planning/TIER_1_10_PLAN_AND_TODO.md`.

---

## 🛠️ Quick Start

### Prerequisites
- Java 17+
- Gradle 8.0+
- Python 3.8+ (for task router)

### Build the Project
```bash
cd primus-platform
./gradlew build
```

### Task Router (Assign work to agents)
```bash
# Install dependencies
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt

# Show next 3 ready tasks (respects dependencies)
python tools/strategy_task_router.py --status strategy.status.example.json next --limit 3

# Generate Copilot prompt for a specific task
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent copilot

# Generate Claude prompt for a specific task
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent claude

# Show all blocked/ready tasks
python tools/strategy_task_router.py --status strategy.status.example.json deps
```

### Run Tests
```bash
cd primus-platform
./gradlew test

# Or specific module
./gradlew :primus-platform:primus-server:test
```

---

## 📋 Key Files to Review

### For Executives & Product Owners
1. **[EXECUTIVE_SUMMARY.md](./primus-platform/EXECUTIVE_SUMMARY.md)** - 30 min read
   - Quick facts, timeline, resource needs, and recommendations
2. **[strategy.MD](./strategy.MD)** - 20 min read
   - Module strategy with entry/exit criteria

### For Architects & Tech Leads
1. **[PRIMUS_ARCHITECTURE.md](./primus-platform/PRIMUS_ARCHITECTURE.md)** - 1 hour
   - Deep-dive on system design, flows, data models
2. **[docs/01-architecture.md](./primus-platform/docs/01-architecture.md)** - Design patterns
3. **[MISSING_MODULES_ANALYSIS.md](./primus-platform/docs/MISSING_MODULES_ANALYSIS.md)** - Gap details

### For Developers / Contributors
1. **[strategy.MD](./strategy.MD)** - Understand the module breakdown
2. **[strategy.tasks.yaml](./strategy.tasks.yaml)** - Agent-consumable backlog
3. **[docs/02-gradle-modules.md](./primus-platform/docs/02-gradle-modules.md)** - Build structure
4. **[docs/03-contracts.md](./primus-platform/docs/03-contracts.md)** - API contracts
5. **Per-tier planning docs**: `primus-platform/docs/planning/TIER_*.md`

### For Operations & DevOps
1. **[docs/12-deployment.md](./primus-platform/docs/12-deployment.md)** - Infrastructure
2. **[docs/05-security.md](./primus-platform/docs/05-security.md)** - Security posture
3. **[docs/11-sequence-diagrams.md](./primus-platform/docs/11-sequence-diagrams.md)** - Flows

---

## 💡 How to Use This Strategy

### For Managers: Assign Work
The `strategy.tasks.yaml` file breaks down all incomplete work into discrete, ordered tasks with:
- Dependency graph (respects module prerequisites)
- Entry criteria (preconditions)
- Exit criteria (definition of done)
- Tier classification (priority)

Use the **task router** to find the next ready task:
```bash
python tools/strategy_task_router.py next --limit 5
```

Then assign to a developer with the auto-generated prompt.

### For Developers: Implement a Module
1. Check `strategy.MD` for your module's **entry criteria** (prerequisites)
2. Review the corresponding **tier plan** (e.g., `TIER_2_MS_PLAN_AND_TODO.md`)
3. Follow the **exit criteria** checklist
4. Use the generated **agent prompt** to guide implementation steps

### For Agents (Copilot/Claude)
The tool auto-generates contextualized prompts:
```bash
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent claude > task.prompt
```

Then feed the prompt to your agent. It will:
- Scaffold the module in Gradle
- Implement domain models, APIs, and tests
- Add observability and security controls
- Update documentation

---

## 🔐 Security & Governance

Primus is **not** for public data. It is designed to safely handle:
- 🔴 **Sensitive PII**: SSN, credit cards, DOB, addresses, banking info
- 🔴 **Regulatory compliance**: SOX, HIPAA, GDPR, PCI-DSS
- 🔴 **Audit trails**: Who accessed what data, when, and why
- 🔴 **Approval workflows**: Multi-level sign-off for sensitive exports

**Critical modules for security**:
- `primus-audit` (compliance trail)
- `primus-approval` (governance gates)
- `primus-api-gateway` (request validation, rate limiting)
- `primus-security` (auth, encryption, masking)

---

## 📊 Module Dependency Graph

```
  Applications (Customers of Primus)
       │
       ├─────────────────┬────────────────┐
       │                 │                │
       ▼                 ▼                ▼
  primus-sdk       primus-cli        primus-ui
       │                 │                │
       └────────┬────────┴─────┬──────────┴─────┐
                │              │                │
        ┌───────▼──────────┐   │                │
        │   primus-core    │   │                │
        │ primus-security  │   │                │
        └────────┬──────────┘   │                │
                 │              │                │
        ┌────────▼────────┐     │                │
        │  primus-export  │     │                │
        │ primus-retrieval│     │                │
        └────────┬─────────┘    │                │
                 │              │                │
        ┌────────┴──────┬───────┴──────┬────────┴──────┐
        │               │              │               │
        ▼               ▼              ▼               ▼
    primus-server  primus-approval  primus-audit  primus-notification
        │               │              │               │
        ├───────────────┴──────────────┴───────────────┤
        │                                              │
        ▼                                              ▼
    primus-nas      primus-storage-s3     primus-monitoring
        │                   │                      │
        │                   ▼                      │
        │         primus-storage-database          │
        │                   │                      │
        └───────────────────┴──────────────────────┘

🎯 Critical Path for v1.0:
    Tier 1 baseline
         ↓
    T2: audit + approval + governance
         ↓
    T3: storage abstraction (S3 + DB)
         ↓
    T4/T5: notification + gateway
         ↓
    Release v1.0 ✅
```

---

## 🔗 Key Documentation

### Architecture
- [Platform Overview](./primus-platform/docs/00-overview.md)
- [System Architecture](./primus-platform/PRIMUS_ARCHITECTURE.md)
- [REST API Reference](./primus-platform/docs/10-rest-api.md)
- [Sequence Diagrams](./primus-platform/docs/11-sequence-diagrams.md)

### Implementation
- [Gradle Modules Guide](./primus-platform/docs/02-gradle-modules.md)
- [Contracts & API Design](./primus-platform/docs/03-contracts.md)
- [YAML Reference](./primus-platform/docs/09-yaml-reference.md)
- [UI Generation](./primus-platform/docs/08-ui-generation.md)

### Roadmap & Planning
- [Tier 1-10 Roadmap](./primus-platform/docs/planning/TIER_1_10_PLAN_AND_TODO.md)
- [Per-Tier Implementation Plans](./primus-platform/docs/planning/)

### Deployment & Ops
- [Deployment Guide](./primus-platform/docs/12-deployment.md)
- [Security Controls](./primus-platform/docs/05-security.md)
- [Storage Strategies](./primus-platform/docs/04-storage.md)

---

## 📞 Support & Contribution

### For Questions
1. Check the relevant tier plan in `primus-platform/docs/planning/`
2. Review the module-specific documentation in `primus-platform/docs/modules/`
3. Read the strategy file for your module in `strategy.MD`

### To Contribute
1. Pick a **ready task** from `strategy.tasks.yaml`
2. Review the **entry criteria** and **exit criteria** in `strategy.MD`
3. Follow the **tier plan** for detailed requirements
4. Run tests and update docs (part of exit criteria)
5. Check off the **exit criteria** and mark the task done

### To Implement a New Module
1. Use the **task router** to find next ready module:
   ```bash
   python tools/strategy_task_router.py next --agent copilot
   ```
2. Copy the generated prompt and feed it to your coding agent or use it as a guide
3. Verify all **exit criteria** are met before marking complete

---

## 📈 Current Status (July 2026)

| Component | Status | Notes |
|-----------|--------|-------|
| **Core export/retrieval** | ✅ | Stable, contract-tested |
| **Metadata framework** | ✅ | `@PrimusExport`, `@PrimusRetrieval` |
| **NAS storage** | ✅ | Works for DEV/QA, doesn't scale |
| **Dynamic UI** | ✅ | Angular-based, metadata-driven |
| **Authentication** | ✅ | JWT, basic RBAC |
| **Audit trail** | ❌ | CRITICAL missing (Tier 2) |
| **Approval workflows** | ❌ | CRITICAL missing (Tier 2) |
| **Multi-cloud storage** | ❌ | CRITICAL missing (Tier 3) |
| **Notifications** | ❌ | CRITICAL missing (Tier 4) |
| **Rate limiting/gateway** | ❌ | CRITICAL missing (Tier 5) |

**Timeline to production-ready**: 8-10 weeks with proper team (3 backend engineers)

---

## 📝 License & Attribution

This project is part of the **Primus Platform** research initiative.

---

## 🎯 Next Steps

1. **Read** [EXECUTIVE_SUMMARY.md](./primus-platform/EXECUTIVE_SUMMARY.md) (30 min)
2. **Review** [strategy.MD](./strategy.MD) (20 min)
3. **Run** the task router to see ready modules:
   ```bash
   source .venv/bin/activate
   python tools/strategy_task_router.py next --limit 3
   ```
4. **Assign** the first ready task to a developer or agent
5. **Track progress** using the status file

---

**Built with ❤️ for enterprise data governance.**

