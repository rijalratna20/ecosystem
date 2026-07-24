# primus-auth-client

Authentication and authorization client library.

## Overview

`primus-auth-client` provides JWT and OAuth2 authentication mechanisms for connecting applications to Primus services.

## Quick Start

### Maven/Gradle Dependency

```gradle
dependencies {
    implementation project(':primus-platform:primus-auth-client')
}
```

### JWT Authentication

```java
JwtAuthClient jwtClient = JwtAuthClient.builder()
    .secretKey("your-secret-key")
    .issuer("primus")
    .audience("primus-platform")
    .build();

// Generate token
String token = jwtClient.generateToken(
    new User("user123", "admin")
);

// Verify token
Claims claims = jwtClient.verifyToken(token);
String userId = claims.getSubject();
```

### OAuth2 Authentication

```java
OAuth2Client oauth2Client = OAuth2Client.builder()
    .clientId("client_id")
    .clientSecret("client_secret")
    .tokenEndpoint("https://auth.example.com/oauth2/token")
    .build();

// Exchange code for token
String token = oauth2Client.exchangeCodeForToken("auth_code");

// Refresh token
String newToken = oauth2Client.refreshToken(refreshToken);
```

## Key Components

### JwtAuthClient
```java
String generateToken(User user)
String generateToken(User user, Duration expiration)
Claims verifyToken(String token)
boolean isTokenExpired(String token)
boolean isTokenValid(String token)
```

### OAuth2Client
```java
String exchangeCodeForToken(String code)
String refreshToken(String refreshToken)
UserInfo getUserInfo(String token)
boolean revokeToken(String token)
```

### RBACClient
```java
boolean hasPermission(String userId, String resource, String action)
List<String> getUserRoles(String userId)
boolean hasRole(String userId, String role)
```

## Configuration

### JWT Configuration

```properties
primus.auth.jwt.secret-key=${JWT_SECRET_KEY}
primus.auth.jwt.issuer=primus
primus.auth.jwt.audience=primus-platform
primus.auth.jwt.expiration=3600
```

### OAuth2 Configuration

```properties
primus.auth.oauth2.client-id=${OAUTH2_CLIENT_ID}
primus.auth.oauth2.client-secret=${OAUTH2_CLIENT_SECRET}
primus.auth.oauth2.token-endpoint=https://auth.example.com/oauth2/token
primus.auth.oauth2.user-info-endpoint=https://auth.example.com/oauth2/userinfo
```

## Testing

```bash
./gradlew :primus-platform:primus-auth-client:test
```

## Module Dependencies

- `primus-common`

## Usage Examples

### Protect a REST Endpoint

```java
@RestController
@RequestMapping("/api/v1")
public class ExportController {
    
    @Autowired
    private JwtAuthClient authClient;
    
    @PostMapping("/export")
    public ResponseEntity<ExportResponse> export(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody ExportRequest request) {
        
        // Verify token
        String token = authHeader.replace("Bearer ", "");
        Claims claims = authClient.verifyToken(token);
        String userId = claims.getSubject();
        
        // Check permission
        if (!hasPermission(userId, "export")) {
            return ResponseEntity.status(403).build();
        }
        
        // Process export
        return ResponseEntity.ok(doExport(request));
    }
}
```

### Client-Side Authentication

```java
// Get token
String token = jwtClient.generateToken(currentUser);

// Make authenticated request
PrimusClient client = PrimusClient.builder()
    .baseUrl("https://primus.example.com")
    .jwtToken(token)
    .build();

ExportResponse response = client.export(exportRequest);
```

## Error Handling

```java
try {
    Claims claims = authClient.verifyToken(token);
} catch (ExpiredJwtException e) {
    System.err.println("Token expired: " + e.getClaims().getExpiration());
} catch (SignatureException e) {
    System.err.println("Invalid signature");
} catch (MalformedJwtException e) {
    System.err.println("Invalid token format");
}
```

## Security Best Practices

✅ Store secrets in environment variables, not code  
✅ Use HTTPS for all token transmission  
✅ Set appropriate token expiration times  
✅ Validate signatures on every token  
✅ Refresh tokens before expiration  
✅ Revoke tokens on logout  

## Troubleshooting

**Issue**: "Invalid token signature"
- **Solution**: Verify secret key matches on both client and server

**Issue**: "Token expired"
- **Solution**: Refresh the token or generate a new one

**Issue**: "User not authorized"
- **Solution**: Check user roles and permissions in RBAC system

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: Foundation  
**Last Updated**: July 2026

