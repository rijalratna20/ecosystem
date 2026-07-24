# primus-api-gateway

API gateway with routing, rate limiting, and authentication.

## Overview

`primus-api-gateway` serves as the central entry point for all Primus API traffic, providing routing, rate limiting, request validation, security controls, and audit-friendly logging before requests reach backend services.

## Module Status

🔴 **NOT YET IMPLEMENTED (Tier 5 - Phase 1)**

**Target Launch**: Week 12-14 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: CRITICAL - Blocks external API access  

## Critical Requirements

### Entry Criteria (Before Starting)
- ✅ Gateway policy model finalized (rate limit, payload rules, TLS/security headers)
- ✅ Depends on: `primus-server` (backend), auth strategy

### Exit Criteria (Definition of Done)
- [ ] External traffic routed through gateway
- [ ] Rate and payload constraints enforced and tested
- [ ] Gateway audit-friendly logs + alerts available

## Purpose

Provides production API protection:
- **Request routing**: Smart routing to backend services
- **Rate limiting**: Prevent abuse and DDoS
- **Authentication**: JWT/OAuth2 validation
- **Authorization**: Policy enforcement
- **Audit logging**: All requests logged and traceable

## Key Features (Planned)

### Request Routing
```
GET /primus/api/v1/exports/123    → primus-server:8080/api/v1/exports/123
POST /primus/api/v1/audits        → primus-audit:8080/api/v1/audits
  (After Tier 2)
```

### Rate Limiting

#### Per-User
- 1,000 requests per hour
- 100 requests per minute
- Backoff for sustained violations

#### Per-Application
- 10,000 requests per hour
- 1,000 requests per minute

#### Per-IP
- 5,000 requests per hour
- 500 requests per minute
- Strict for unknown IPs

### Authentication & Authorization
- JWT token validation
- Scope checking
- OAuth2 token exchange
- API key alternative

### Request Validation
- Content-type checking
- Payload size limits (max 10MB)
- JSON schema validation
- SQL injection detection

## APIs (Planned)

```
# All traffic flows through gateway
https://primus.example.com/api/v1/exports
https://primus.example.com/api/v1/retrievals
https://primus.example.com/api/v1/applications
```

## Configuration (Planned)

```properties
# Core
primus.gateway.enabled=true
primus.gateway.port=443  # HTTPS only
primus.gateway.protocol=https

# TLS
primus.gateway.tls.certfile=/etc/ssl/primus.crt
primus.gateway.tls.keyfile=/etc/ssl/primus.key
primus.gateway.tls.min-version=TLSv1.2

# Rate Limiting
primus.gateway.ratelimit.per-user-per-hour=1000
primus.gateway.ratelimit.per-user-per-minute=100
primus.gateway.ratelimit.per-app-per-hour=10000
primus.gateway.ratelimit.per-ip-per-hour=5000

# Request Validation
primus.gateway.validation.max-payload-size=10mb
primus.gateway.validation.require-content-type=true
primus.gateway.validation.enable-sql-filter=true

# Authentication
primus.gateway.auth.jwt.enabled=true
primus.gateway.auth.jwt.secret=${JWT_SECRET}
primus.gateway.auth.oauth2.enabled=true
primus.gateway.auth.api-key.enabled=true

# Logging
primus.gateway.logging.enabled=true
primus.gateway.logging.log-headers=true
primus.gateway.logging.log-payloads=false  # For PII protection
```

## Technical Design (Planned)

### Architecture
```
Internet
    ↓
Load Balancer (optional)
    ↓
primus-api-gateway [HTTPS]
    ↓
Request Processing:
  1. TLS/SSL termination
  2. Authentication
  3. Rate limit check
  4. Payload validation
  5. Audit logging
  6. Route to backend
    ↓
primus-server / primus-audit / primus-approval
(Internal network)
    ↓
Response Processing:
  1. Audit response code
  2. Add security headers
  3. Return to client
    ↓
Client
```

### Rate Limiting Strategy
- Token bucket algorithm
- Redis-backed for distributed deployments
- Configurable quotas per tier
- Graceful degradation (queue vs. reject)

### Audit Logging
```
2026-07-23T10:00:00.123Z | 
  user_123 | 
  POST /api/v1/exports | 
  200 OK | 
  1.234ms | 
  req_abc123 (correlation ID)
```

## Security Features

✅ **TLS 1.2+** - Encrypted transport  
✅ **JWT validation** - Token verification  
✅ **Rate limiting** - DDoS prevention  
✅ **Request validation** - Injection prevention  
✅ **Audit logs** - Full traceability  
✅ **Security headers** - XSS/CSRF protection  

Headers added:
```
Strict-Transport-Security: max-age=31536000
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
```

## Performance Targets

- **Latency**: <50ms p95 (added by gateway)
- **Throughput**: 10,000+ requests/second
- **Availability**: 99.99% uptime

## Deployment

### Docker
```dockerfile
FROM nginx:latest
# Install lua/openresty for rate limiting
# Configure routing rules
# TLS certificates
```

### Kubernetes
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: primus-gateway
spec:
  ingressClassName: nginx
  tls:
    - hosts: ['primus.example.com']
      secretName: primus-tls
  rules:
    - host: primus.example.com
      http:
        paths:
          - path: /api/v1
            backend:
              service:
                name: primus-server
                port: 8080
```

## Testing Strategy

### Load Tests
- 10,000 concurrent users
- Sustained 5,000 req/s
- Spike to 10,000 req/s

### Security Tests
- SQL injection attempts
- Rate limit bypass attempts
- Token tampering
- Unauthorized access attempts

### Integration Tests
- End-to-end request flow
- Route correctness
- Timeout handling
- Error responses

## Related Modules

- **primus-server** (Core) — Backend service
- **primus-audit** (Tier 2) — Logs requests
- **primus-monitoring** (Tier 6) — Metrics collection

---

**Tier**: 5 (API Protection - Phase 1)  
**Status**: 🔴 NOT STARTED  
**Priority**: CRITICAL  
**Target Start**: September 3, 2026  
**Target Completion**: September 17, 2026

