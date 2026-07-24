# Tier 5 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 5 focuses on API protection and scalability. The goal is to introduce gateway controls, harden request handling, and validate platform performance under enterprise load.

## Tier 5 Outcomes (Target)
- API gateway policy enforcement in front of core services
- Rate limits and payload controls active
- Traceable request path with correlation context
- Load and resilience baselines established

## Microservices in Tier 5

### 1) `primus-api-gateway` (New Edge Layer)
**Role**
- Entry control point for all external API traffic

**Required Features**
- Authentication pass-through and token checks
- Rate limiting and quota enforcement
- Request size/content validation
- Routing and health-aware backend forwarding

### 2) `primus-server` and Core APIs (Hardening Scope)
**Role**
- Enforce strict validation and bounded execution behavior

**Required Features**
- Input validation and schema enforcement
- Timeout and circuit-breaker-friendly behavior
- Correlation ID propagation
- Standard security response semantics

## Tier 5 TODO List

### A. Gateway Delivery
- [ ] Define gateway routing and policy model
- [ ] Implement rate limiting by user/IP/app
- [ ] Enforce payload size and content-type restrictions
- [ ] Add TLS and secure header policies

### B. Backend Hardening
- [ ] Add strict validation for all public endpoints
- [ ] Add bounded timeout and retry-safe semantics
- [ ] Align all error responses to security-safe patterns

### C. Scale and Reliability Validation
- [ ] Run load tests for steady-state traffic
- [ ] Run stress tests for peak behavior
- [ ] Run soak tests for memory/resource stability
- [ ] Document bottlenecks and tuning actions

### D. Security Operations
- [ ] Add abuse/attack detection alerts
- [ ] Add audit-friendly request logging at gateway layer
- [ ] Publish edge security runbook

## Exit Criteria for Tier 5
- [ ] Gateway policies enforce rate and payload constraints
- [ ] Core APIs pass validation/security hardening checklist
- [ ] Performance baselines captured under expected load
- [ ] Incident response guidance available for API protection events

