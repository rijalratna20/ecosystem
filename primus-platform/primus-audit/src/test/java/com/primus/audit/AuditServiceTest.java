package com.primus.audit;

import com.primus.audit.domain.AuditEvent;
import com.primus.audit.domain.AuditEventType;
import com.primus.audit.repository.AuditRepository;
import com.primus.audit.service.AuditService;
import com.primus.audit.service.DefaultAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new DefaultAuditService(new InMemoryAuditRepository());
    }

    @Test
    void record_and_query_by_application() {
        AuditEvent event = AuditEvent.builder()
                .id("evt-1")
                .type(AuditEventType.EXPORT_COMPLETED)
                .applicationId("app-A")
                .principalId("user-1")
                .outcome("SUCCESS")
                .build();

        auditService.record(event);

        List<AuditEvent> results = auditService.queryByApplication("app-A",
                Instant.EPOCH, Instant.now().plusSeconds(60));
        assertEquals(1, results.size());
        assertEquals("evt-1", results.get(0).getId());
    }

    @Test
    void record_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> auditService.record(null));
    }

    @Test
    void sensitive_access_query() {
        AuditEvent s = AuditEvent.builder()
                .id("s-1")
                .type(AuditEventType.SENSITIVE_FIELD_ACCESSED)
                .principalId("user-2")
                .outcome("SUCCESS")
                .build();
        auditService.record(s);

        List<AuditEvent> results = auditService.querySensitiveAccess(Instant.EPOCH, Instant.now().plusSeconds(60));
        assertEquals(1, results.size());
    }

    @Test
    void compliance_report_contains_counts() {
        auditService.record(AuditEvent.builder().id("e1").type(AuditEventType.EXPORT_COMPLETED).outcome("OK").build());
        auditService.record(AuditEvent.builder().id("r1").type(AuditEventType.RETRIEVAL_COMPLETED).outcome("OK").build());

        String report = auditService.generateComplianceReport(Instant.EPOCH, Instant.now().plusSeconds(60));
        assertTrue(report.contains("\"exports\":1"));
        assertTrue(report.contains("\"retrievals\":1"));
    }

    // ---- minimal in-memory repository for testing ----

    static class InMemoryAuditRepository implements AuditRepository {
        private final List<AuditEvent> store = new ArrayList<>();

        @Override public void save(AuditEvent event) { store.add(event); }

        @Override
        public List<AuditEvent> findByApplicationId(String id, Instant from, Instant to) {
            return store.stream().filter(e -> id.equals(e.getApplicationId())
                    && !e.getOccurredAt().isBefore(from) && !e.getOccurredAt().isAfter(to))
                    .collect(Collectors.toList());
        }

        @Override
        public List<AuditEvent> findByType(AuditEventType type, Instant from, Instant to) {
            return store.stream().filter(e -> e.getType() == type
                    && !e.getOccurredAt().isBefore(from) && !e.getOccurredAt().isAfter(to))
                    .collect(Collectors.toList());
        }

        @Override
        public List<AuditEvent> findByPrincipalId(String id, Instant from, Instant to) {
            return store.stream().filter(e -> id.equals(e.getPrincipalId())
                    && !e.getOccurredAt().isBefore(from) && !e.getOccurredAt().isAfter(to))
                    .collect(Collectors.toList());
        }

        @Override
        public List<AuditEvent> findAll(Instant from, Instant to) {
            return store.stream().filter(e -> !e.getOccurredAt().isBefore(from) && !e.getOccurredAt().isAfter(to))
                    .collect(Collectors.toList());
        }
    }
}
