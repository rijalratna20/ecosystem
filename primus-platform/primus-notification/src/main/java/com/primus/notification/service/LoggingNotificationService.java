package com.primus.notification.service;

import com.primus.notification.domain.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Logging-only implementation of {@link NotificationService}.
 * Useful for tests and environments where no real channel is configured.
 */
public class LoggingNotificationService implements NotificationService {

    private final List<Notification> sent = new ArrayList<>();

    @Override
    public void send(Notification notification) {
        if (notification == null) throw new NotificationException("notification must not be null");
        sent.add(notification);
        System.out.printf("[NOTIFICATION] channel=%s recipient=%s subject=%s%n",
                notification.getChannel(), notification.getRecipient(), notification.getSubject());
    }

    @Override
    public String enqueue(Notification notification) {
        send(notification);
        return UUID.randomUUID().toString();
    }

    /** Returns all notifications sent so far (for assertions in tests). */
    public List<Notification> getSent() {
        return Collections.unmodifiableList(sent);
    }

    public void clear() {
        sent.clear();
    }
}
