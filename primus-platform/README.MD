What Problem Does Primus Solve?
Executive Summary

Primus is a secure, metadata-driven data replication platform that enables production-like data to be safely exported, stored, and retrieved for lower environments (DEV, QA, UAT, Performance, Training) without requiring custom code for each application.

Instead of every application building its own scripts, SQL exports, masking logic, storage conventions, and UI, Primus provides a standardized platform where applications describe what data they own, and Primus handles how it is exported, secured, stored, searched, and retrieved.

Problems Today

Imagine a company has 100 microservices.

Customer Service

Loan Service

Payment Service

Fraud Service

Mortgage Service

Collections Service

Document Service

...

Every team eventually needs production data in lower environments.

Unfortunately, every team solves it differently.

Typical approaches:

Manual MongoDB export
SQL scripts
Custom REST endpoints
Shell scripts
Shared folders
SFTP
Email requests
Database backups
Direct production access

Every solution becomes unique.

Problems Organizations Face
1. Production data cannot safely be copied

Production contains:

SSN
Credit card numbers
Driver licenses
Names
Addresses
DOB
Income
Banking information

Simply copying a database is a security risk.

2. Every application exports data differently

One team writes:

exportCustomer.sh

Another team writes

mongoexport

Another builds

GET /customer/export

Nothing is standardized.

3. No common metadata

Nobody knows

what collections exist
relationships
root entity
child collections
document format

Knowledge lives inside developers' heads.

4. Difficult to reconstruct a transaction

Example

Customer

↓

Accounts

↓

Applications

↓

Documents

↓

Audit

↓

Credit Bureau

↓

Notifications

Which collections belong together?

Every application answers differently.

5. Sensitive fields are inconsistent

One application masks

SSN

Another forgets.

One masks

DOB

Another exposes it.

No centralized rules.

6. Lower environments become stale

Developers need:

Production Customer

↓

QA

↓

Bug Reproduction

But data arrives days later.

7. UI is hardcoded

Every application builds

Search Page

↓

Result Grid

↓

Detail Page

↓

Tabs

↓

Filters

Again and again.

What Primus Introduces

Instead of code...

Developers provide metadata.

metadata.yaml

query.yaml

sensitiveField.yaml

That's it.

Everything else becomes automatic.

Primus Responsibilities
1. Application Registration

Client application says

I am Loan Service.

These are my collections.

These are my relationships.

These are my search fields.

Primus validates and registers the application.

2. Secure Export

Developer writes

@PrimusExport

Primus automatically

collects related collections
masks sensitive data
converts to JSON/XML
uploads securely
retries failures
records audit logs

No export logic is written by the application.

3. Secure Storage

Primus stores

LoanApp

2026200

customer

12345.json

account

12345.json

bureau

12345.xml

using a standardized folder structure.

4. Metadata Driven Search

Instead of writing

SELECT ...

WHERE ...

the application supplies

query.yaml

Primus generates the search UI and retrieval APIs.

5. Dynamic UI

Instead of Angular developers building pages,

Primus generates

Search

Result Grid

Filters

Tabs

Collection Viewer

directly from metadata.

Adding a new application requires no new Angular code.

6. Standard Retrieval

Developers write

@PrimusRetrieval

Primus retrieves

Customer

Account

Credit

Audit

XML

Images

and assembles the complete transaction.

7. Governance

Primus knows

every registered application
metadata versions
exported transactions
upload history
permissions
audit logs

This enables enterprise governance instead of ad hoc scripts.

Benefits for Developers

Without Primus:

Write export logic

Write REST API

Write storage

Write retry

Write JWT

Write search page

Write result page

Write detail page

Write masking

Write retrieval

With Primus:

metadata.yaml

query.yaml

sensitiveField.yaml

@PrimusExport

@PrimusRetrieval

Done.

Benefits for Operations
Consistent storage structure
Centralized governance
Auditable exports
Versioned metadata
Easier backups
Standard APIs
Reduced maintenance
Benefits for Security
JWT authentication
Centralized authorization
Sensitive field masking
No direct production database access
Audit trail for every export and retrieval
Configurable approval workflows
Benefits for the Enterprise

Primus creates a common platform for all applications:

Standardized integration: Every application exports and retrieves data the same way.
Faster onboarding: New applications only need metadata and annotations instead of custom tooling.
Reduced duplication: Teams no longer build and maintain their own export, masking, storage, and retrieval solutions.
Consistent user experience: Search and detail screens are generated from metadata, giving every application a familiar interface.
Governance and compliance: Centralized metadata, versioning, auditing, and security policies make the platform easier to operate and review.
Primus Vision

Primus is a metadata-driven data virtualization platform that standardizes how enterprise applications securely publish, store, search, and retrieve production-like business transactions for lower environments.

By replacing custom export scripts, hardcoded retrieval APIs, and manually developed UIs with configuration, annotations, and shared platform services, Primus enables organizations to deliver consistent, secure, and reusable production data management across all applications.