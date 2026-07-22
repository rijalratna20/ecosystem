package com.primus.server.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Append-only audit service. Records governance events for all export, retrieval,
 * and approval actions (Tier 2).
 *
 * <p>In-memory implementation for bootstrapping. Replace with a persistent store
 * (append-only DB table or event log) in Tier 2 production hardening.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    /** Thread-safe append-only store. */
    private final List<AuditEvent> store = new CopyOnWriteArrayList<>();

    /**
     * Record an audit event. Events are never updated or deleted.
     */
    public AuditEvent recordEvent(String action, String actor, String appId,
                                  String targetId, String correlationId) {
        AuditEvent event = new AuditEvent(
                UUID.randomUUID().toString(),
                action, actor, appId, targetId, correlationId, "RECORDED");
        store.add(event);
        log.info("AUDIT action={} actor={} appId={} targetId={} correlationId={}",
                action, actor, appId, targetId, correlationId);
        return event;
    }

    /**
     * Query audit events for a specific target (export/retrieval ID).
     */
    public List<AuditEvent> findByTarget(String targetId) {
        return store.stream()
                .filter(e -> targetId.equals(e.getTargetId()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Query audit events by actor.
     */
    public List<AuditEvent> findByActor(String actor) {
        return store.stream()
                .filter(e -> actor.equals(e.getActor()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Query audit events for an application.
     */
    public List<AuditEvent> findByApp(String appId) {
        return store.stream()
                .filter(e -> appId.equals(e.getAppId()))
                .collect(Collectors.toUnmodifiableList());
    }

    /** Return a snapshot of all events (read-only). */
    public List<AuditEvent> all() {
        return Collections.unmodifiableList(new ArrayList<>(store));
    }

    public int size() { return store.size(); }
}
