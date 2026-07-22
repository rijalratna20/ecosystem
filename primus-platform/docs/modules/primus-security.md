# primus-security Module

## Purpose

Central security library providing JWT-based authentication, OAuth2 support, Role-Based Access Control (RBAC), and cryptographic helpers. All Primus services delegate their authentication and authorization logic to this module.

## Module Location

```
primus-platform/primus-libs/primus-security/
```

## Responsibilities

✓ **JWT Token Management**
- Generate signed access tokens and refresh tokens
- Validate token signatures and expiry
- Extract claims (user, roles, permissions) from tokens
- Support token rotation / revocation (deny-list pattern)

✓ **OAuth2 Integration**
- OAuth2 Client Credentials flow (service-to-service)
- OAuth2 Authorization Code flow (interactive login)
- Token introspection against an identity provider
- PKCE support for single-page application flows

✓ **Role-Based Access Control (RBAC)**
- Role and permission definitions
- Permission evaluation (user ↔ resource ↔ action)
- Role hierarchy (ADMIN > EDITOR > VIEWER)
- Field-level access control (mask fields per role)

✓ **Cryptographic Helpers**
- Symmetric encryption / decryption (AES-256-GCM)
- Password hashing and verification (bcrypt / argon2)
- Digital signature generation and verification (RS256)
- Secure random value generation

✓ **Security Context**
- Thread-local security context propagation
- Reactive (Webflux) context propagation
- Audit principal extraction

## Public APIs

### JWT Provider

```java
public interface JwtProvider {
    /** Generate a short-lived access token. */
    String generateAccessToken(PrimusUser user);

    /** Generate a long-lived refresh token. */
    String generateRefreshToken(PrimusUser user);

    /** Validate a token and return its parsed claims. */
    TokenClaims validate(String token) throws TokenExpiredException, InvalidTokenException;

    /** Revoke a token (deny-list entry). */
    void revoke(String token);

    /** Check whether a token has been revoked. */
    boolean isRevoked(String token);
}

public class TokenClaims {
    private String subject;          // User ID
    private String username;
    private List<String> roles;
    private List<String> permissions;
    private Instant issuedAt;
    private Instant expiresAt;
    private Map<String, Object> custom;
}
```

### Authorization Service

```java
public interface AuthorizationService {
    /**
     * Returns true if the current authenticated user holds the given role.
     */
    boolean hasRole(String role);

    /**
     * Returns true if the user has the specified permission on the resource.
     * @param resource e.g. "exports"
     * @param action   e.g. "create", "read", "delete"
     */
    boolean hasPermission(String resource, String action);

    /**
     * Throws AuthorizationException if permission check fails.
     */
    void enforce(String resource, String action);

    /**
     * Return the list of fields accessible for the current user/role.
     */
    Set<String> allowedFields(String contractName);
}
```

### Security Context

```java
public class SecurityContext {
    /** Get the authenticated user from the current thread. */
    public static PrimusUser currentUser();

    /** Get raw JWT claims. */
    public static TokenClaims currentClaims();

    /** Programmatically set context (integration tests / service calls). */
    public static void setContext(PrimusUser user);

    /** Clear the context (end of request). */
    public static void clear();
}
```

### Encryption Helpers

```java
public class CryptoUtils {
    /** AES-256-GCM encrypt. Returns Base64-encoded ciphertext + IV. */
    public static String encrypt(String plaintext, SecretKey key);

    /** AES-256-GCM decrypt. */
    public static String decrypt(String ciphertext, SecretKey key);

    /** Hash a password using bcrypt. */
    public static String hashPassword(String password);

    /** Verify a plain-text password against a bcrypt hash. */
    public static boolean verifyPassword(String password, String hash);

    /** Generate a cryptographically secure random token (hex). */
    public static String generateSecureToken(int bytes);
}
```

### Permission Matrix

| Role | Read | Create | Update | Delete | Admin |
|------|------|--------|--------|--------|-------|
| VIEWER | ✅ | ❌ | ❌ | ❌ | ❌ |
| EDITOR | ✅ | ✅ | ✅ | ❌ | ❌ |
| OPERATOR | ✅ | ✅ | ✅ | ✅ | ❌ |
| ADMIN | ✅ | ✅ | ✅ | ✅ | ✅ |

## Packages

```
com.primus.security
├── jwt/                   # JWT token lifecycle
│   ├── JwtProvider
│   ├── JwtProviderImpl
│   ├── TokenClaims
│   └── TokenDenyList
├── oauth2/                # OAuth2 flows
│   ├── OAuth2ClientService
│   ├── OAuth2TokenIntrospector
│   └── PkceHelper
├── rbac/                  # Role-based access control
│   ├── AuthorizationService
│   ├── AuthorizationServiceImpl
│   ├── RoleHierarchy
│   ├── PermissionEvaluator
│   └── FieldAccessFilter
├── context/               # Security context propagation
│   ├── SecurityContext
│   └── SecurityContextInterceptor
├── crypto/                # Cryptographic utilities
│   ├── CryptoUtils
│   ├── KeyManager
│   └── PasswordEncoder
└── model/                 # Security domain models
    ├── PrimusUser
    ├── Role
    └── Permission
```

## Dependencies

- **primus-common** – exceptions (`AuthException`, `AuthorizationException`)
- External:
  - Spring Security
  - jjwt (JWT parsing/generation)
  - Bouncy Castle (cryptography)
  - Spring OAuth2 Client

## Configuration

```yaml
primus:
  security:
    jwt:
      secret: ${JWT_SECRET}           # Min 256-bit secret
      accessTokenExpiry: 3600         # seconds (1 hour)
      refreshTokenExpiry: 604800      # seconds (7 days)
      issuer: primus-platform

    oauth2:
      enabled: true
      provider: keycloak
      tokenUri: ${OAUTH2_TOKEN_URI}
      jwkSetUri: ${OAUTH2_JWK_SET_URI}
      clientId: ${OAUTH2_CLIENT_ID}
      clientSecret: ${OAUTH2_CLIENT_SECRET}

    rbac:
      enforceFieldMasking: true
      defaultRole: VIEWER
```

## Security Context Lifecycle

```
Request arrives
    │
    ▼
JWT filter reads "Authorization: ******"
    │
    ▼
JwtProvider.validate(token) → TokenClaims
    │
    ▼
SecurityContext.setContext(user)   ← thread-local populated
    │
    ▼
Controller / Service runs
    │ AuthorizationService.enforce("exports", "create")
    ▼
Response sent → SecurityContext.clear()
```

## Extension Points

### Custom Permission Evaluator

```java
@Component
public class ContractPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean evaluate(PrimusUser user, String resource, String action) {
        // Custom logic – e.g., contract-level ownership checks
        return user.getOwnedContracts().contains(resource);
    }
}
```

### Custom Field Access Filter

```java
@Component
public class SensitiveFieldFilter implements FieldAccessFilter {
    @Override
    public Set<String> allowedFields(String contractName, PrimusUser user) {
        if (user.getRoles().contains("COMPLIANCE_OFFICER")) {
            return Set.of("*");  // full access
        }
        return sensitiveFieldRegistry.nonSensitiveFieldsFor(contractName);
    }
}
```

## Future Enhancements

- [ ] HSM (Hardware Security Module) key storage integration
- [ ] SAML 2.0 federation support
- [ ] Fine-grained attribute-based access control (ABAC)
- [ ] Automatic key rotation
- [ ] Multi-factor authentication (TOTP/WebAuthn)
- [ ] Audit log for every permission decision

## Testing

```bash
./gradlew :primus-platform:primus-libs:primus-security:test
```

Key test scenarios:
- Token generation → validation → expiry
- Revoked token rejection
- Role hierarchy evaluation
- Field masking per role
- Encryption round-trip

## Build & Publish

```bash
./gradlew :primus-platform:primus-libs:primus-security:build
./gradlew :primus-platform:primus-libs:primus-security:publishToMavenLocal
```

---

**Related:**
- [primus-authenticator.md](./primus-authenticator.md) – annotation-level auth enforcement
- [05-security.md](../05-security.md) – platform-wide security architecture
- [primus-common.md](./primus-common.md) – shared exceptions
