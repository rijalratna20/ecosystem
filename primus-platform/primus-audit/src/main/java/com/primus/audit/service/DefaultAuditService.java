package com.primus.audit.service;

import com.primus.audit.domain.AuditEvent;
import com.primus.audit.domain.AuditEventType;
import com.primus.audit.repository.AuditRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default in-process implementation of {@link AuditService}.
 * Delegates persistence to an {@link AuditRepository}.
 */
public class DefaultAuditService implements AuditService {

    private final AuditRepository repository;

    public DefaultAuditService(AuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(AuditEvent event) {
        if (event == null) throw new IllegalArgumentException("event must not be null");
        repository.save(event);
    }

    @Override
    public List<AuditEvent> queryByApplication(String applicationId, Instant from, Instant to) {
        return repository.findByApplicationId(applicationId, from, to);
    }

    @Override
    public List<AuditEvent> queryByType(AuditEventType type, Instant from, Instant to) {
        return repository.findByType(type, from, to);
    }

    @Override
    public List<AuditEvent> queryByPrincipal(String principalId, Instant from, Instant to) {
        return repository.findByPrincipalId(principalId, from, to);
    }

    @Override
    public List<AuditEvent> querySensitiveAccess(Instant from, Instant to) {
        return repository.findByType(AuditEventType.SENSITIVE_FIELD_ACCESSED, from, to);
    }

    @Override
    public String generateComplianceReport(Instant from, Instant to) {
        List<AuditEvent> events = repository.findAll(from, to);
        long exports = events.stream().filter(e -> e.getType() == AuditEventType.EXPORT_COMPLETED).count();
        long retrievals = events.stream().filter(e -> e.getType() == AuditEventType.RETRIEVAL_COMPLETED).count();
        long sensitiveAccess = events.stream().filter(e -> e.getType() == AuditEventType.SENSITIVE_FIELD_ACCESSED).count();

        return String.format(
                "{\"period\":{\"from\":\"%s\",\"to\":\"%s\"},"
                + "\"summary\":{\"totalEvents\":%d,\"exports\":%d,\"retrievals\":%d,\"sensitiveAccess\":%d}}",
                from, to, events.size(), exports, retrievals, sensitiveAccess);
    }
}
