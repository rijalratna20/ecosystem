package com.primus.approval.service;

import com.primus.approval.domain.ApprovalRequest;
import com.primus.approval.domain.ApprovalStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link ApprovalService}.
 * Replace the backing store with a proper JPA repository in production.
 */
public class DefaultApprovalService implements ApprovalService {

    private final Map<String, ApprovalRequest> store = new ConcurrentHashMap<>();

    @Override
    public ApprovalRequest createRequest(ApprovalRequest request) {
        if (request == null) throw new IllegalArgumentException("request must not be null");
        store.put(request.getId(), request);
        return request;
    }

    @Override
    public ApprovalRequest approve(String requestId, String reviewedBy, String note) {
        ApprovalRequest req = requireFound(requestId);
        req.approve(reviewedBy, note);
        return req;
    }

    @Override
    public ApprovalRequest reject(String requestId, String reviewedBy, String note) {
        ApprovalRequest req = requireFound(requestId);
        req.reject(reviewedBy, note);
        return req;
    }

    @Override
    public List<ApprovalRequest> listByStatus(ApprovalStatus status) {
        return store.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalRequest findById(String requestId) {
        return store.get(requestId);
    }

    @Override
    public int expireOverdueRequests() {
        Instant now = Instant.now();
        int count = 0;
        for (ApprovalRequest req : store.values()) {
            if (req.getStatus() == ApprovalStatus.PENDING && req.getSlaDeadline().isBefore(now)) {
                req.expire();
                count++;
            }
        }
        return count;
    }

    private ApprovalRequest requireFound(String id) {
        ApprovalRequest req = store.get(id);
        if (req == null) throw new IllegalArgumentException("Approval request not found: " + id);
        return req;
    }
}
