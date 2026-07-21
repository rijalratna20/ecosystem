package com.primus.notification.template;

import com.primus.notification.domain.Notification;
import com.primus.notification.domain.NotificationChannel;
import com.primus.notification.domain.NotificationEventType;

import java.util.Map;

/**
 * Factory for well-known notification templates described in the planning document.
 *
 * <p>Templates:
 * <ul>
 *   <li>ExportReady   - "Your export [ID] is ready: download_link"</li>
 *   <li>ApprovalPending - "New sensitive export requires approval: link"</li>
 *   <li>ExportFailed  - "Export [ID] failed: reason"</li>
 *   <li>HighVolume    - "Alert: 1000+ exports in last hour"</li>
 * </ul>
 */
public final class NotificationTemplates {

    private NotificationTemplates() {}

    public static Notification exportReady(String id, String recipient, String exportId, String downloadLink) {
        return Notification.builder()
                .id(id)
                .eventType(NotificationEventType.EXPORT_READY)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Your export is ready")
                .body("Your export [" + exportId + "] is ready. Download: " + downloadLink)
                .build();
    }

    public static Notification approvalPending(String id, String recipient, String approvalId, String approvalLink) {
        return Notification.builder()
                .id(id)
                .eventType(NotificationEventType.APPROVAL_PENDING)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Approval required for sensitive data export")
                .body("New sensitive export requires your approval. Review: " + approvalLink)
                .metadata(Map.of("approvalId", approvalId))
                .build();
    }

    public static Notification exportFailed(String id, String recipient, String exportId, String reason) {
        return Notification.builder()
                .id(id)
                .eventType(NotificationEventType.EXPORT_FAILED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Export failed: " + exportId)
                .body("Export [" + exportId + "] failed. Reason: " + reason)
                .build();
    }

    public static Notification highVolumeAlert(String id, String recipient, int count, int windowMinutes) {
        return Notification.builder()
                .id(id)
                .eventType(NotificationEventType.HIGH_VOLUME_ALERT)
                .channel(NotificationChannel.SLACK)
                .recipient(recipient)
                .subject("High volume alert")
                .body("Alert: " + count + "+ exports in the last " + windowMinutes + " minutes.")
                .build();
    }
}
