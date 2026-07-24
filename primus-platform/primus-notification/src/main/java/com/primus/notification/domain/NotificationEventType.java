package com.primus.notification.domain;

/** Well-known notification event types. */
public enum NotificationEventType {
    EXPORT_READY,
    EXPORT_FAILED,
    APPROVAL_PENDING,
    APPROVAL_GRANTED,
    APPROVAL_REJECTED,
    HIGH_VOLUME_ALERT,
    SYSTEM_ERROR
}
