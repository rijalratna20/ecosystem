package com.primus.audit.service;

import com.primus.audit.domain.AuditEvent;
import com.primus.audit.domain.AuditEventType;

import java.time.Instant;
import java.util.List;

/**
 * Central service for recording and querying audit events.
 * All writes are append-only; no update or delete operations are exposed.
 *
 * <p>REST mapping (implemented in primus-server or a dedicated audit service):
 * <pre>
 *   POST /api/v1/audit/log           – record event (internal use only)
 *   GET  /api/v1/audit/exports       – query export events
 *   GET  /api/v1/audit/retrievals    – query retrieval events
 *   GET  /api/v1/audit/sensitive     – query sensitive-field access events
 *   GET  /api/v1/audit/reports       – generate compliance reports
 * </pre>
 */
public interface AuditService {

    /**
     * Record a single audit event (append-only).
     *
     * @param event the event to record; must not be null
     */
    void record(AuditEvent event);

    /**
     * Query audit events for a specific application within a time range.
     *
     * @param applicationId the application identifier
     * @param from          start of the time range (inclusive)
     * @param to            end of the time range (inclusive)
     * @return ordered list of matching events, oldest first
     */
    List<AuditEvent> queryByApplication(String applicationId, Instant from, Instant to);

    /**
     * Query audit events by event type within a time range.
     *
     * @param type event type filter
     * @param from start of the time range (inclusive)
     * @param to   end of the time range (inclusive)
     * @return ordered list of matching events, oldest first
     */
    List<AuditEvent> queryByType(AuditEventType type, Instant from, Instant to);

    /**
     * Query all events involving a specific principal (user or service account).
     *
     * @param principalId the principal identifier
     * @param from        start of the time range
     * @param to          end of the time range
     * @return ordered list of matching events
     */
    List<AuditEvent> queryByPrincipal(String principalId, Instant from, Instant to);

    /**
     * Retrieve events in which sensitive fields were accessed.
     * Used for HIPAA / GDPR investigations.
     *
     * @param from start of the time range
     * @param to   end of the time range
     * @return list of SENSITIVE_FIELD_ACCESSED events
     */
    List<AuditEvent> querySensitiveAccess(Instant from, Instant to);

    /**
     * Generate a compliance summary report for the given time range.
     * The format is implementation-specific (JSON, PDF, CSV).
     *
     * @param from start of the time range
     * @param to   end of the time range
     * @return serialized report content
     */
    String generateComplianceReport(Instant from, Instant to);
}
