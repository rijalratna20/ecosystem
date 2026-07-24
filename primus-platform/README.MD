# What Problem Does Primus Solve?

## Executive Summary

Primus is a secure, metadata-driven data replication platform that enables production-like data to be safely exported, stored, and retrieved for lower environments (DEV, QA, UAT, Performance, Training) without requiring custom code for each application.

Instead of every application building its own scripts, SQL exports, masking logic, storage conventions, and UI, Primus provides a standardized platform where applications describe what data they own, and Primus handles how it is exported, secured, stored, searched, and retrieved.

## Problems Today

Imagine a company with 100 microservices:

- Customer Service
- Loan Service
- Payment Service
- Fraud Service
- Mortgage Service
- Collections Service
- Document Service
- ...and many more

Every team eventually needs production data in lower environments, but each team solves it differently.

Typical approaches include:

- Manual MongoDB export
- SQL scripts
- Custom REST endpoints
- Shell scripts
- Shared folders
- SFTP
- Email requests
- Database backups
- Direct production access

Every solution becomes unique.

## Problems Organizations Face

1. **Production data cannot safely be copied**
   - Production contains sensitive data such as SSN, credit card numbers, driver licenses, names, addresses, DOB, income, and banking information.
   - Simply copying a database is a security risk.

2. **Every application exports data differently**
   - One team writes `exportCustomer.sh`.
   - Another team uses `mongoexport`.
   - Another builds `GET /customer/export`.
   - Nothing is standardized.

3. **No common metadata**
   - Teams lack a shared definition of:
	 - Existing collections
	 - Relationships
	 - Root entities
	 - Child collections
	 - Document formats
   - Knowledge lives in developers' heads.

4. **Difficult to reconstruct a transaction**
   - Example flow:
	 - Customer -> Accounts -> Applications -> Documents -> Audit -> Credit Bureau -> Notifications
   - Which collections belong together is often unclear.

5. **Sensitive fields are handled inconsistently**
   - One application masks SSN; another forgets.
   - One masks DOB; another exposes it.
   - No centralized rules exist.

6. **Lower environments become stale**
   - Teams need fast flow from production customer data to QA bug reproduction.
   - Data often arrives days later.

7. **UI is hardcoded repeatedly**
   - Each application rebuilds:
	 - Search page
	 - Result grid
	 - Detail page
	 - Tabs
	 - Filters

## What Primus Introduces

Instead of writing custom code, developers provide metadata:

- `metadata.yaml`
- `query.yaml`
- `sensitiveField.yaml`

That is all. Everything else becomes automatic.

## Primus Responsibilities

1. **Application registration**
   - Client applications declare:
	 - Identity (for example, "I am Loan Service")
	 - Collections
	 - Relationships
	 - Search fields
   - Primus validates and registers the application.

2. **Secure export**
   - Developers annotate with `@PrimusExport`.
   - Primus automatically:
	 - Collects related collections
	 - Masks sensitive data
	 - Converts to JSON/XML
	 - Uploads securely
	 - Retries failures
	 - Records audit logs
   - No export logic is written by the application.

3. **Secure storage**
   - Primus stores data with a standardized folder structure, for example:
	 - `LoanApp/2026200/customer/12345.json`
	 - `LoanApp/2026200/account/12345.json`
	 - `LoanApp/2026200/bureau/12345.xml`

4. **Metadata-driven search**
   - Instead of writing SQL like `SELECT ... WHERE ...`, applications supply `query.yaml`.
   - Primus generates search UI and retrieval APIs.

5. **Dynamic UI generation**
   - Primus generates:
	 - Search
	 - Result grid
	 - Filters
	 - Tabs
	 - Collection viewer
   - Adding a new application requires no new Angular code.

6. **Standard retrieval**
   - Developers annotate with `@PrimusRetrieval`.
   - Primus retrieves customer, account, credit, audit, XML, and image artifacts, then assembles a complete transaction.

7. **Governance**
   - Primus tracks:
	 - Registered applications
	 - Metadata versions
	 - Exported transactions
	 - Upload history
	 - Permissions
	 - Audit logs
   - This enables enterprise governance instead of ad hoc scripts.

## Benefits for Developers

Without Primus, teams must build and maintain export logic, REST APIs, storage, retries, JWT integration, search pages, result pages, detail pages, masking, and retrieval.

With Primus, teams mainly provide:

- `metadata.yaml`
- `query.yaml`
- `sensitiveField.yaml`
- `@PrimusExport`
- `@PrimusRetrieval`

Done.

## Benefits for Operations

- Consistent storage structure
- Centralized governance
- Auditable exports
- Versioned metadata
- Easier backups
- Standard APIs
- Reduced maintenance

## Benefits for Security

- JWT authentication
- Centralized authorization
- Sensitive field masking
- No direct production database access
- Audit trail for every export and retrieval
- Configurable approval workflows

## Benefits for the Enterprise

Primus creates a common platform for all applications:

- **Standardized integration:** Every application exports and retrieves data the same way.
- **Faster onboarding:** New applications need metadata and annotations instead of custom tooling.
- **Reduced duplication:** Teams no longer build and maintain separate export, masking, storage, and retrieval solutions.
- **Consistent user experience:** Search and detail screens are generated from metadata, giving every application a familiar interface.
- **Governance and compliance:** Centralized metadata, versioning, auditing, and security policies make the platform easier to operate and review.

## Primus Vision

Primus is a metadata-driven data virtualization platform that standardizes how enterprise applications securely publish, store, search, and retrieve production-like business transactions for lower environments.

By replacing custom export scripts, hardcoded retrieval APIs, and manually developed UIs with configuration, annotations, and shared platform services, Primus enables organizations to deliver consistent, secure, and reusable production data management across all applications.