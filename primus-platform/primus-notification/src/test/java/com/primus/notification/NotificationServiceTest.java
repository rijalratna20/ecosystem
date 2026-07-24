package com.primus.notification;

import com.primus.notification.domain.Notification;
import com.primus.notification.domain.NotificationChannel;
import com.primus.notification.domain.NotificationEventType;
import com.primus.notification.service.LoggingNotificationService;
import com.primus.notification.service.NotificationException;
import com.primus.notification.template.NotificationTemplates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private LoggingNotificationService service;

    @BeforeEach
    void setUp() {
        service = new LoggingNotificationService();
    }

    @Test
    void send_export_ready() {
        Notification n = NotificationTemplates.exportReady("n1", "user@example.com", "exp-123", "https://download/123");
        service.send(n);
        assertEquals(1, service.getSent().size());
        assertEquals(NotificationEventType.EXPORT_READY, service.getSent().get(0).getEventType());
    }

    @Test
    void enqueue_returns_tracking_id() {
        Notification n = NotificationTemplates.approvalPending("n2", "reviewer@example.com", "req-1", "https://approve/1");
        String trackingId = service.enqueue(n);
        assertNotNull(trackingId);
        assertFalse(trackingId.isBlank());
    }

    @Test
    void high_volume_uses_slack_channel() {
        Notification n = NotificationTemplates.highVolumeAlert("n3", "#ops-alerts", 1000, 60);
        service.send(n);
        assertEquals(NotificationChannel.SLACK, service.getSent().get(0).getChannel());
    }

    @Test
    void send_null_throws() {
        assertThrows(NotificationException.class, () -> service.send(null));
    }

    @Test
    void export_failed_template_body_contains_reason() {
        Notification n = NotificationTemplates.exportFailed("n4", "user@example.com", "exp-999", "DB timeout");
        assertTrue(n.getBody().contains("DB timeout"));
    }
}
