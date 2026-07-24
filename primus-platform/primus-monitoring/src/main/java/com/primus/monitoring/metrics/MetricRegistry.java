package com.primus.monitoring.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lightweight in-process counter registry for export/retrieval metrics.
 *
 * <p>Key metrics tracked:
 * <ul>
 *   <li>export.success, export.failure, export.duration_ms</li>
 *   <li>retrieval.success, retrieval.failure, retrieval.latency_ms</li>
 *   <li>storage.errors</li>
 * </ul>
 *
 * In production, push these metrics to Micrometer / Prometheus / CloudWatch.
 */
public class MetricRegistry {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    /** Increment a named counter by 1. */
    public void increment(String name) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
    }

    /** Increment a named counter by a specific amount (e.g. duration in ms). */
    public void add(String name, long amount) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).addAndGet(amount);
    }

    /** Get the current value of a counter, or 0 if it has never been incremented. */
    public long get(String name) {
        AtomicLong c = counters.get(name);
        return c == null ? 0L : c.get();
    }

    /** Reset a counter to zero. */
    public void reset(String name) {
        counters.computeIfPresent(name, (k, v) -> { v.set(0); return v; });
    }

    /** Return a snapshot of all counters. */
    public Map<String, Long> snapshot() {
        Map<String, Long> snap = new ConcurrentHashMap<>();
        counters.forEach((k, v) -> snap.put(k, v.get()));
        return snap;
    }
}
