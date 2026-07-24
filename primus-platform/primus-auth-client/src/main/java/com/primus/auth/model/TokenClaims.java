package com.primus.auth.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Parsed claims from a validated bearer token.
 */
public class TokenClaims {

    private final String subject;          // user ID / service account
    private final String issuer;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final List<PrimusRole> roles;
    private final String appId;            // optional app-scoped token

    public TokenClaims(String subject, String issuer, Instant issuedAt, Instant expiresAt,
                       List<PrimusRole> roles, String appId) {
        this.subject = subject;
        this.issuer = issuer;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.roles = roles == null ? Collections.emptyList() : Collections.unmodifiableList(roles);
        this.appId = appId;
    }

    public String getSubject() { return subject; }
    public String getIssuer() { return issuer; }
    public Instant getIssuedAt() { return issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public List<PrimusRole> getRoles() { return roles; }
    public String getAppId() { return appId; }

    public boolean hasRole(PrimusRole role) { return roles.contains(role); }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
