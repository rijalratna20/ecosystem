package com.primus.auth;

import com.primus.auth.model.PrimusRole;
import com.primus.auth.model.TokenClaims;
import com.primus.auth.validator.TokenValidator;
import com.primus.common.exception.AuthException;
import com.primus.common.exception.AuthorizationException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidatorTest {

    private final TokenValidator validator = new TokenValidator("primus-test", 60);

    private String token(String subject, String... roles) {
        String encoded = Base64.getEncoder().encodeToString(subject.getBytes());
        if (roles.length == 0) return encoded + ":";
        return encoded + ":" + String.join(",", roles);
    }

    @Test
    void validToken_returnsAdminClaims() {
        String raw = token("user1", "ADMIN");
        TokenClaims claims = validator.validate(raw);
        assertEquals("user1", claims.getSubject());
        assertTrue(claims.hasRole(PrimusRole.ADMIN));
    }

    @Test
    void bearerPrefix_stripped() {
        String raw = "Bearer " + token("alice", "OPERATOR");
        TokenClaims claims = validator.validate(raw);
        assertEquals("alice", claims.getSubject());
    }

    @Test
    void missingToken_throwsAuthException() {
        assertThrows(AuthException.class, () -> validator.validate(null));
        assertThrows(AuthException.class, () -> validator.validate(""));
    }

    @Test
    void requireRole_passes() {
        TokenClaims claims = validator.validate(token("bob", "ADMIN"));
        assertDoesNotThrow(() -> validator.requireRole(claims, PrimusRole.OPERATOR));
    }

    @Test
    void requireRole_fails() {
        TokenClaims claims = validator.validate(token("charlie", "VIEWER"));
        assertThrows(AuthorizationException.class,
                () -> validator.requireRole(claims, PrimusRole.ADMIN));
    }
}
