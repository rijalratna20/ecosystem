package com.primus.notification.service;

import com.primus.notification.domain.Notification;

/**
 * Core notification dispatch service.
 *
 * <p>Implementations can be channel-specific (SMTP, Slack HTTP, Twilio)
 * or composite (fan-out to multiple channels based on user preferences).
 */
public interface NotificationService {

    /**
     * Send a notification immediately.
     *
     * @param notification the notification to send
     * @throws NotificationException if delivery fails
     */
    void send(Notification notification);

    /**
     * Queue a notification for asynchronous delivery.
     *
     * @param notification the notification to enqueue
     * @return a delivery tracking id
     */
    String enqueue(Notification notification);
}
