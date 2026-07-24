package com.primus.approval;

import com.primus.approval.domain.ApprovalRequest;
import com.primus.approval.domain.ApprovalStatus;
import com.primus.approval.service.ApprovalService;
import com.primus.approval.service.DefaultApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalServiceTest {

    private ApprovalService service;

    @BeforeEach
    void setUp() {
        service = new DefaultApprovalService();
    }

    private ApprovalRequest newRequest(String id) {
        return new ApprovalRequest(id, "tx-" + id, "app-1", "user-1",
                "UAT testing", Instant.now(), Instant.now().plusSeconds(86400), List.of("ssn", "creditCard"));
    }

    @Test
    void create_and_approve() {
        service.createRequest(newRequest("req-1"));
        ApprovalRequest result = service.approve("req-1", "reviewer-1", "Looks good");
        assertEquals(ApprovalStatus.APPROVED, result.getStatus());
        assertEquals("reviewer-1", result.getReviewedBy());
    }

    @Test
    void create_and_reject() {
        service.createRequest(newRequest("req-2"));
        ApprovalRequest result = service.reject("req-2", "reviewer-1", "Insufficient justification");
        assertEquals(ApprovalStatus.REJECTED, result.getStatus());
    }

    @Test
    void list_pending() {
        service.createRequest(newRequest("req-3"));
        service.createRequest(newRequest("req-4"));
        service.approve("req-4", "r", "ok");

        List<ApprovalRequest> pending = service.listByStatus(ApprovalStatus.PENDING);
        assertEquals(1, pending.size());
        assertEquals("req-3", pending.get(0).getId());
    }

    @Test
    void expire_overdue() {
        ApprovalRequest overdue = new ApprovalRequest("req-5", "tx-5", "app-1", "user-1",
                "reason", Instant.now().minusSeconds(10), Instant.now().minusSeconds(1), List.of());
        service.createRequest(overdue);

        int expired = service.expireOverdueRequests();
        assertEquals(1, expired);
        assertEquals(ApprovalStatus.EXPIRED, service.findById("req-5").getStatus());
    }

    @Test
    void double_approve_throws() {
        service.createRequest(newRequest("req-6"));
        service.approve("req-6", "r", "ok");
        assertThrows(IllegalStateException.class,
                () -> service.approve("req-6", "r2", "again"));
    }
}
