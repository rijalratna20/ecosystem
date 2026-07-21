package com.primus.audit.repository;

import com.primus.audit.domain.AuditEvent;
import com.primus.audit.domain.AuditEventType;

import java.time.Instant;
import java.util.List;

/**
 * Append-only persistence layer for audit events.
 * Implementations may use a relational database, an immutable log store,
 * or a write-once object store (S3 / NAS).
 */
public interface AuditRepository {

    /** Persist one event.  Must never overwrite or delete existing events. */
    void save(AuditEvent event);

    List<AuditEvent> findByApplicationId(String applicationId, Instant from, Instant to);

    List<AuditEvent> findByType(AuditEventType type, Instant from, Instant to);

    List<AuditEvent> findByPrincipalId(String principalId, Instant from, Instant to);

    List<AuditEvent> findAll(Instant from, Instant to);
}
