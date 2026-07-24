package com.primus.auth.model;

/**
 * Roles supported by the Primus platform.
 * Role names map to permissions in the authorization policy.
 */
public enum PrimusRole {

    /** Read-only access to exports and retrieval results. */
    VIEWER,

    /** Can initiate exports and retrievals. */
    OPERATOR,

    /** Can approve sensitive export/retrieval requests. */
    APPROVER,

    /** Access to audit and compliance views. */
    COMPLIANCE,

    /** Full administrative access. */
    ADMIN;

    public boolean hasAtLeast(PrimusRole required) {
        return this.ordinal() >= required.ordinal();
    }
}
