package com.primus.notification.service;

/** Thrown when a notification cannot be delivered. */
public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
