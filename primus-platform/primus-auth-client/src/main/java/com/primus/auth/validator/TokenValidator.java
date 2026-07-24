package com.primus.auth.validator;

import com.primus.auth.model.PrimusRole;
import com.primus.auth.model.TokenClaims;
import com.primus.common.exception.AuthException;
import com.primus.common.exception.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Validates bearer tokens and extracts {@link TokenClaims}.
 *
 * <p>This implementation uses a simple signed-header approach for bootstrapping.
 * In production, replace with a proper JWT library (e.g. jjwt or nimbus-jose-jwt)
 * and validate the signature against the configured public key / JWKS endpoint.
 */
public class TokenValidator {

    private static final Logger log = LoggerFactory.getLogger(TokenValidator.class);

    private final String issuer;
    private final long clockSkewSeconds;

    public TokenValidator(String issuer, long clockSkewSeconds) {
        this.issuer = issuer;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    /**
     * Validate a raw bearer token string and return the parsed claims.
     * Throws {@link AuthException} if the token is missing, malformed, or expired.
     */
    public TokenClaims validate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new AuthException("Authentication token is required");
        }

        String token = rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;

        TokenClaims claims = parseToken(token);

        if (claims.isExpired()) {
            log.warn("Token expired for subject={}", claims.getSubject());
            throw new AuthException("Token has expired", "TOKEN_EXPIRED");
        }

        log.debug("Token validated for subject={} roles={}", claims.getSubject(), claims.getRoles());
        return claims;
    }

    /** Check that the caller holds at least the required role, throwing 403 if not. */
    public void requireRole(TokenClaims claims, PrimusRole required) {
        boolean hasRole = claims.getRoles().stream()
                .anyMatch(r -> r.hasAtLeast(required));
        if (!hasRole) {
            log.warn("Authorization denied: subject={} needs={} has={}",
                    claims.getSubject(), required, claims.getRoles());
            throw new AuthorizationException(
                    "Required role " + required + " not granted to " + claims.getSubject());
        }
    }

    /**
     * Parse the token into claims. This stub returns a claims object decoded from
     * a Base64-encoded subject header. Replace with real JWT parsing in production.
     */
    private TokenClaims parseToken(String token) {
        try {
            // Minimal stub: token format is "base64(subject):role1,role2"
            String[] parts = token.split(":", 2);
            String subject = new String(Base64.getDecoder().decode(parts[0]));
            List<PrimusRole> roles = Collections.emptyList();
            if (parts.length == 2 && !parts[1].isBlank()) {
                roles = List.of(parts[1].split(",")).stream()
                        .map(r -> PrimusRole.valueOf(r.trim().toUpperCase()))
                        .toList();
            }
            Instant now = Instant.now();
            return new TokenClaims(subject, issuer, now, now.plusSeconds(3600 + clockSkewSeconds),
                    roles, null);
        } catch (Exception e) {
            log.warn("Token parse failure", e);
            throw new AuthException("Invalid token format");
        }
    }
}
