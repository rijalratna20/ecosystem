# Security Framework

## Overview

Primus implements a comprehensive security model covering authentication, authorization, encryption, and audit logging.

## Authentication

### Supported Methods

#### 1. API Key Authentication
- **Use Case**: Service-to-service communication
- **Format**: Header-based token
- **Example**:
```
Authorization: Bearer sk_live_xxxxx
```

#### 2. OAuth2
- **Use Case**: User-facing applications
- **Flow**: Authorization Code, Client Credentials
- **Scopes**: read, write, admin

#### 3. JWT (JSON Web Token)
- **Use Case**: Stateless authentication
- **Claims**: user_id, roles, permissions, exp
- **Encryption**: RS256, HS256

### Authentication Configuration

```yaml
security:
  authentication:
    enabled: true
    methods:
      - apiKey
      - oauth2
      - jwt
    jwtSecret: ${JWT_SECRET}
    jwtExpiration: 3600
```

## Authorization (RBAC)

### Role-Based Access Control

```
Admin
├── Can access all resources
├── Can modify contracts
└── Can view audit logs

Editor
├── Can view and export data
├── Can modify own contracts
└── Can view own audit logs

Viewer
└── Read-only access to assigned resources
```

### Permission Model

```yaml
permissions:
  - resource: "exports"
    actions: [create, read, update, delete]
    roles: [admin, editor]
  - resource: "contracts"
    actions: [create, read, update]
    roles: [admin, editor]
  - resource: "reports"
    actions: [read]
    roles: [admin, viewer]
```

## Encryption

### Encryption Standards

- **Algorithm**: AES-256-GCM
- **Key Management**: AWS KMS, HashiCorp Vault
- **In-Transit**: TLS 1.3
- **At-Rest**: File/database-level encryption

### Encrypted Fields

```java
@ExportField
@Encrypted(algorithm = "AES256")
private String personalId;

@ExportField
@Encrypted
private String bankAccount;
```

## Audit Logging

### Logged Events

- User authentication (success/failure)
- Authorization checks
- Data access and modifications
- Configuration changes
- Export and retrieval operations

### Log Format

```json
{
  "timestamp": "2024-01-15T10:30:45Z",
  "user_id": "user123",
  "action": "EXPORT_DATA",
  "resource": "UserProfileExport",
  "result": "SUCCESS",
  "ip_address": "192.168.1.100",
  "user_agent": "Mozilla/5.0..."
}
```

## Security Best Practices

1. **Rotate credentials**: API keys, JWT secrets quarterly
2. **Use HTTPS**: Always in production
3. **Validate input**: Prevent injection attacks
4. **Rate limiting**: Protect against brute force
5. **Least privilege**: Assign minimal required permissions
6. **Monitor logs**: Regular audit log review

