package com.primus.audit.domain;

/**
 * Classification of audit-worthy events in the Primus platform.
 */
public enum AuditEventType {
    EXPORT_INITIATED,
    EXPORT_COMPLETED,
    EXPORT_FAILED,
    RETRIEVAL_REQUESTED,
    RETRIEVAL_COMPLETED,
    SENSITIVE_FIELD_ACCESSED,
    APPROVAL_REQUESTED,
    APPROVAL_GRANTED,
    APPROVAL_REJECTED,
    AUTH_SUCCESS,
    AUTH_FAILURE,
    CONFIG_CHANGED,
    DATA_DELETED
}
