# Primus Platform - Complete Documentation

Welcome to the Primus Platform documentation. Start here to understand the system architecture and find specific module details.

## Documentation Structure

### Phase 1: Platform Overview
Learn about Primus at a high level:

1. **[00-overview.md](./00-overview.md)** - What is Primus?
   - Problems it solves
   - Platform concepts & terminology
   - High-level architecture

2. **[01-architecture.md](./01-architecture.md)** - How does Primus work?
   - System architecture diagrams
   - Deployment architecture
   - Component diagrams
   - Data flows (export & retrieval)
   - Technology stack
   - Module dependency graph

3. **[02-gradle-modules.md](./02-gradle-modules.md)** - What are the modules?
   - Complete module table
   - Dependencies & relationships
   - Build commands
   - How to add new modules

### Phase 2: Module Deep-Dives
Detailed reference for each module:

Each module document follows this structure:
- **Purpose** - Why does this module exist?
- **Responsibilities** - What does it do?
- **Public APIs** - What can you call?
- **Packages** - How is it organized?
- **Dependencies** - What does it depend on?
- **Configuration** - How to configure it?
- **Sequence** - How does data flow through it?
- **Extension Points** - How to extend it?
- **Future Enhancements** - What's planned?

**Library Modules:**
- [primus-common.md](./modules/primus-common.md) - Shared DTOs, utilities, exceptions
- [primus-annotations.md](./modules/primus-annotations.md) - Custom annotations framework
- [primus-export.md](./modules/primus-export.md) - Export engine & pipeline
- [primus-retrieval.md](./modules/primus-retrieval.md) - Retrieval engine & caching

**Service Modules:**
- [primus-server.md](./modules/primus-server.md) - Main microservice
- [primus-nas.md](./modules/primus-nas.md) - NAS storage integration
- [shadow-server.md](./modules/shadow-server.md) - Testing & validation service
- [primus-authenticator.md](./modules/primus-authenticator.md) - Auth framework
- [primus-client-sdk.md](./modules/primus-client-sdk.md) - Client SDK for apps

**UI Modules:**
- [primus-ui.md](./modules/primus-ui.md) - Main Angular application
- [shadow-ui.md](./modules/shadow-ui.md) - Experimental UI for testing

**Utilities:**
- [yaml-generator.md](./modules/yaml-generator.md) - YAML contract generation

### Phase 3: Detailed Specifications

**Contracts & YAML:**
- [03-contracts.md](./03-contracts.md) - Data contract examples
- [09-yaml-reference.md](./09-yaml-reference.md) - Complete YAML schema reference

**REST API:**
- [10-rest-api.md](./10-rest-api.md) - Full REST API reference
  - Every endpoint
  - Request/response examples
  - Error codes
  - Authorization requirements

**System Flows:**
- [06-export-flow.md](./06-export-flow.md) - Complete export workflow
- [07-retrieval-flow.md](./07-retrieval-flow.md) - Complete retrieval workflow
- [11-sequence-diagrams.md](./11-sequence-diagrams.md) - Sequence diagrams (Mermaid)

**Infrastructure:**
- [04-storage.md](./04-storage.md) - Storage backends overview
- [05-security.md](./05-security.md) - Security architecture
- [08-ui-generation.md](./08-ui-generation.md) - Dynamic UI generation
- [12-deployment.md](./12-deployment.md) - Deployment guide

## Quick Start for Developers

### First Time Here?
1. Read [00-overview.md](./00-overview.md) - 5 minutes
2. Review [01-architecture.md](./01-architecture.md) - 10 minutes
3. Skim [02-gradle-modules.md](./02-gradle-modules.md) - 5 minutes

### I need to...

**...understand the platform**
- [00-overview.md](./00-overview.md) - Concepts & terminology
- [01-architecture.md](./01-architecture.md) - Architecture & flows

**...work on a specific module**
- Find in [modules/](./modules/) directory
- Each has Purpose, APIs, Configuration, Sequence

**...integrate with Primus**
- [primus-client-sdk.md](./modules/primus-client-sdk.md) - How to consume Primus
- [10-rest-api.md](./10-rest-api.md) - REST endpoints

**...define a data contract**
- [09-yaml-reference.md](./09-yaml-reference.md) - YAML schema
- [03-contracts.md](./03-contracts.md) - Examples

**...export or retrieve data**
- [06-export-flow.md](./06-export-flow.md) - Export workflow
- [07-retrieval-flow.md](./07-retrieval-flow.md) - Retrieval workflow
- [11-sequence-diagrams.md](./11-sequence-diagrams.md) - Visual flows

**...deploy Primus**
- [12-deployment.md](./12-deployment.md) - Docker, Kubernetes, production

**...understand security**
- [05-security.md](./05-security.md) - Auth, RBAC, encryption

## Document Map

```
docs/
├── 00-overview.md              ← START HERE
├── 01-architecture.md          ← Then here
├── 02-gradle-modules.md        ← Then here
├── 03-contracts.md
├── 04-storage.md
├── 05-security.md
├── 06-export-flow.md
├── 07-retrieval-flow.md
├── 08-ui-generation.md
├── 09-yaml-reference.md
├── 10-rest-api.md
├── 11-sequence-diagrams.md
├── 12-deployment.md
└── modules/
    ├── primus-common.md
    ├── primus-annotations.md
    ├── primus-export.md
    ├── primus-retrieval.md
    ├── primus-authenticator.md
    ├── primus-client-sdk.md
    ├── primus-nas.md
    ├── primus-server.md
    ├── primus-ui.md
    ├── shadow-server.md
    ├── shadow-ui.md
    └── yaml-generator.md
```

## Common Questions

**Q: What's the relationship between Primus and its modules?**
A: Primus is the platform. It's composed of multiple modules (libraries + services + UIs). See [02-gradle-modules.md](./02-gradle-modules.md) for the module map.

**Q: How do applications use Primus?**
A: They use either:
- The REST API directly ([10-rest-api.md](./10-rest-api.md))
- The primus-client-sdk ([modules/primus-client-sdk.md](./modules/primus-client-sdk.md))

**Q: How do I define what data to export?**
A: You write a YAML contract file. See [09-yaml-reference.md](./09-yaml-reference.md) for the full schema and [03-contracts.md](./03-contracts.md) for examples.

**Q: Where does data get stored?**
A: Multiple backends supported (NAS, S3, PostgreSQL). See [04-storage.md](./04-storage.md).

**Q: How is sensitive data protected?**
A: Encryption at rest & in transit, field-level masking per role. See [05-security.md](./05-security.md).

## Document Conventions

- **Code blocks** show actual working examples
- **Diagrams** are in text format (ASCII) or Mermaid
- **Tables** summarize key information
- **Links** connect related documents

## Version

- **Platform**: v1.0.0
- **Last Updated**: July 16, 2026
- **Java**: 17+
- **Spring Boot**: 3.1+
- **Angular**: 16+

## Need Help?

- Check [Common Questions](#common-questions) above
- Search within a document (Ctrl+F)
- Check the Related sections at bottom of each document
- Review [11-sequence-diagrams.md](./11-sequence-diagrams.md) for visual flows

---

**Next:** Start with [00-overview.md](./00-overview.md)
