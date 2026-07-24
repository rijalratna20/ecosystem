# Tier 3 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 3 focuses on storage abstraction and multi-cloud readiness. The goal is to decouple core flows from NAS-only storage and support pluggable backends with policy-driven routing.

## Tier 3 Outcomes (Target)
- Storage provider SPI introduced and adopted by core services
- NAS implementation aligned to provider interface
- Cloud storage provider available for S3-compatible platforms
- Resilient storage behavior (retry, integrity checks, fallback)

## Microservices in Tier 3

### 1) `primus-storage-core` (New Abstraction Module)
**Role**
- Defines storage provider contract and capability model

**Required Features**
- Provider interface for put/get/list/delete
- Capability flags (versioning, multipart, encryption)
- Unified storage URI model
- Error taxonomy for storage failures

### 2) `primus-nas` (Provider Refactor)
**Role**
- Existing NAS backend aligned to provider interface

**Required Features**
- SPI-compatible adapter implementation
- Atomic writes and checksums
- Backoff/retry on transient I/O failures

### 3) `primus-storage-s3` (New Cloud Provider)
**Role**
- Cloud object storage backend (S3-compatible)

**Required Features**
- Multipart uploads
- Server-side encryption integration
- Bucket/path policy support
- Lifecycle and retention alignment

### 4) `primus-server` (Routing and Policy Integration)
**Role**
- Chooses storage backend by policy

**Required Features**
- Storage routing policy per application/environment
- Fallback strategy configuration
- Storage selection observability fields in responses

### 5) `primus-export` and `primus-retrieval` (Consumer Integration)
**Role**
- Consume storage via abstraction only

**Required Features**
- No direct NAS calls
- Provider-aware error handling
- Streaming retrieval compatibility

## Tier 3 TODO List

### A. Architecture and Contracts
- [ ] Finalize storage SPI and capability model
- [ ] Define storage URI and metadata contract
- [ ] Define policy model for backend routing and fallback

### B. Backend Implementations
- [ ] Refactor `primus-nas` to `primus-storage-core` SPI
- [ ] Build `primus-storage-s3` provider
- [ ] Add encryption and integrity verification support

### C. Core Service Integration
- [ ] Update `primus-export` to use SPI only
- [ ] Update `primus-retrieval` to use SPI only
- [ ] Add storage route decision logic in `primus-server`

### D. Testing and Reliability
- [ ] Integration tests for NAS and S3 providers
- [ ] Failure injection tests (timeouts, permission errors)
- [ ] Fallback tests between primary and secondary backends
- [ ] Performance baseline for large artifact upload/download

### E. Release Readiness
- [ ] Publish storage migration guide (NAS -> cloud)
- [ ] Publish operational playbook for provider failures

## Exit Criteria for Tier 3
- [ ] Core flows work through SPI with no direct backend coupling
- [ ] NAS and S3 providers pass integration and resilience tests
- [ ] Storage routing and fallback policies validated
- [ ] Storage behavior documented for operators and developers

