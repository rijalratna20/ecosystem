package com.primus.approval.domain;

/** Lifecycle states of an approval request. */
public enum ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED,
    CANCELLED
}
