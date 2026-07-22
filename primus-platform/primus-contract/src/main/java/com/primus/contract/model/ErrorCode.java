package com.primus.contract.model;

/** Platform-wide error codes used in API responses. */
public final class ErrorCode {

    private ErrorCode() {}

    // Validation
    public static final String VALIDATION_ERROR        = "VALIDATION_ERROR";
    public static final String MISSING_REQUIRED_FIELD  = "MISSING_REQUIRED_FIELD";
    public static final String INVALID_FORMAT          = "INVALID_FORMAT";

    // Authentication / Authorization
    public static final String AUTHENTICATION_FAILED   = "AUTHENTICATION_FAILED";
    public static final String TOKEN_EXPIRED           = "TOKEN_EXPIRED";
    public static final String AUTHORIZATION_DENIED    = "AUTHORIZATION_DENIED";

    // Resources
    public static final String NOT_FOUND               = "NOT_FOUND";
    public static final String CONFLICT                = "CONFLICT";

    // Export / Retrieval
    public static final String EXPORT_FAILED           = "EXPORT_FAILED";
    public static final String EXPORT_NOT_READY        = "EXPORT_NOT_READY";
    public static final String RETRIEVAL_FAILED        = "RETRIEVAL_FAILED";

    // Governance
    public static final String APPROVAL_REQUIRED       = "APPROVAL_REQUIRED";
    public static final String APPROVAL_EXPIRED        = "APPROVAL_EXPIRED";
    public static final String POLICY_DENIED           = "POLICY_DENIED";

    // Storage
    public static final String STORAGE_ERROR           = "STORAGE_ERROR";
    public static final String INTEGRITY_CHECK_FAILED  = "INTEGRITY_CHECK_FAILED";

    // General
    public static final String INTERNAL_ERROR          = "INTERNAL_ERROR";
    public static final String SERVICE_UNAVAILABLE     = "SERVICE_UNAVAILABLE";
}
