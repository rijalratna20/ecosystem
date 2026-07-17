# primus-authenticator Module

## Overview

Handles user authentication, authorization, and access control across the Primus platform.

## Module Location

```
primus-platform/primus-annotations/authenticator/
```

## Key Responsibilities

1. **User Authentication**: Verify user credentials
2. **Token Generation**: Issue and validate JWT tokens
3. **Authorization**: Enforce role-based access control (RBAC)
4. **Session Management**: Manage user sessions
5. **Audit Logging**: Track authentication events

## Core Components

### AuthenticationService

```java
public interface AuthenticationService {
    AuthToken authenticate(Credentials credentials);
    void validateToken(String token);
    AuthToken refreshToken(String refreshToken);
    void logout(String token);
}

public class Credentials {
    private String username;
    private String password;
}

public class AuthToken {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
}
```

### AuthorizationService

```java
public interface AuthorizationService {
    boolean hasPermission(User user, String resource, String action);
    boolean hasRole(User user, String role);
    List<Permission> getPermissions(User user);
    void enforcePermission(User user, String resource, String action);
}

public class Permission {
    private String resource;
    private String action;
    private String role;
}
```

### JWT Provider

```java
public interface JwtProvider {
    String generateToken(User user);
    String generateRefreshToken(User user);
    Claims validateToken(String token);
    User getUserFromToken(String token);
}

public class Claims {
    private String subject;
    private List<String> roles;
    private Map<String, Object> claims;
    private Date issuedAt;
    private Date expiresAt;
}
```

## User Model

```java
public class User {
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private UserStatus status; // ACTIVE, INACTIVE, SUSPENDED
    private List<String> roles;
    private List<String> permissions;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}

public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, DELETED
}

public enum UserRole {
    ADMIN,      // Full system access
    EDITOR,     // Create/modify contracts and exports
    VIEWER,     // Read-only access
    EXTERNAL    // Third-party integrations
}
```

## Authentication Flows

### 1. Username/Password Login

```java
@PostMapping("/auth/login")
public ResponseEntity<AuthToken> login(@RequestBody Credentials credentials) {
    AuthToken token = authService.authenticate(credentials);
    return ResponseEntity.ok(token);
}
```

**Request**:
```json
{
  "username": "user@company.com",
  "password": "secure_password"
}
```

**Response**:
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "expiresIn": 3600,
  "tokenType": "bearer"
}
```

### 2. Token Refresh

```java
@PostMapping("/auth/refresh")
public ResponseEntity<AuthToken> refresh(
    @RequestHeader("Authorization") String bearerToken) {
    String token = bearerToken.substring(7);
    AuthToken newToken = authService.refreshToken(token);
    return ResponseEntity.ok(newToken);
}
```

### 3. OAuth2 Flow

```kotlin
oauth2:
  providers:
    - name: google
      clientId: ${GOOGLE_CLIENT_ID}
      clientSecret: ${GOOGLE_CLIENT_SECRET}
      scope: "email profile"
    - name: azure
      clientId: ${AZURE_CLIENT_ID}
      authority: "https://login.microsoftonline.com/..."
```

## Role-Based Access Control (RBAC)

### Role Hierarchy

```
                    ADMIN
                      │
                    EDITOR
                     /   \
                VIEWER  EXTERNAL
```

### Permission Mapping

```yaml
permissions:
  - resource: "exports"
    actions:
      - "create"
      - "read"
      - "update"
      - "delete"
    roles:
      - "ADMIN"
      - "EDITOR"
  
  - resource: "contracts"
    actions:
      - "create"
      - "update"
    roles:
      - "ADMIN"
      - "EDITOR"
  
  - resource: "reports"
    actions:
      - "read"
    roles:
      - "ADMIN"
      - "VIEWER"

  - resource: "exports"
    actions:
      - "read"
    roles:
      - "EXTERNAL"
```

## Authorization Decorator

```java
@PostMapping("/exports")
@Authorization(roles = {"ADMIN", "EDITOR"})
public ResponseEntity<Export> createExport(
    @RequestBody ExportRequest request) {
    // Implementation
}

@GetMapping("/exports/{id}")
@Authorization(permissions = {"READ_EXPORT"})
public ResponseEntity<Export> getExport(@PathVariable String id) {
    // Implementation
}
```

## JWT Token Structure

```json
Header:
{
  "alg": "RS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "user123",
  "username": "user@company.com",
  "email": "user@company.com",
  "roles": ["EDITOR"],
  "permissions": ["READ_EXPORT", "CREATE_EXPORT"],
  "iat": 1705329045,
  "exp": 1705332645,
  "iss": "https://primus.company.com"
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

## Audit Logging

```java
public class AuditLog {
    private String auditId;
    private String userId;
    private String action;     // LOGIN, LOGOUT, UNAUTHORIZED_ACCESS
    private String resource;
    private String result;     // SUCCESS, FAILURE
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
}

@Autowired
private AuditLogger auditLogger;

auditLogger.log(
    user.getId(),
    "LOGIN",
    "auth",
    "SUCCESS",
    request.getRemoteAddr()
);
```

## Security Features

### Password Policy

```yaml
security:
  password:
    minLength: 12
    requireUpperCase: true
    requireLowerCase: true
    requireNumbers: true
    requireSpecialChars: true
    expirationDays: 90
    historyCount: 5
```

### Token Policy

```yaml
security:
  token:
    accessTokenExpirationMinutes: 60
    refreshTokenExpirationDays: 7
    algorithm: RS256
    keyStore: /etc/primus/keystore.jks
    keyAlias: primus-key
```

### Session Management

```yaml
security:
  session:
    maxConcurrentSessions: 5
    sessionTimeoutMinutes: 30
    enableSessionTrace: true
```

## Configuration

```yaml
authentication:
  enabled: true
  
  jwt:
    secret: ${JWT_SECRET}
    expiration: 3600
    refreshExpiration: 604800
  
  oauth2:
    enabled: true
    providers: [google, azure]
  
  password:
    bcryptStrength: 12
    
  audit:
    enabled: true
    logFile: /var/log/primus/audit.log
```

## Testing

```bash
./gradlew :primus-platform:primus-annotations:authenticator:test
```

## Best Practices

1. **Never log passwords**: Only log hashes
2. **Use HTTPS**: Always in production
3. **Rotate secrets**: JWT secrets, encryption keys regularly
4. **Implement rate limiting**: Prevent brute force attacks
5. **Validate tokens**: Check expiration and signature
6. **Use secure defaults**: Strong algorithms, proper key management

