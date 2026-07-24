package com.primus.gateway.ratelimit;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple sliding-window rate limiter keyed by a principal identifier.
 * Uses an in-memory store; swap for Redis in production to support clustering.
 */
public class RateLimiter {

    /** Holds the request count and window-start timestamp for one key. */
    private static final class Window {
        AtomicInteger count = new AtomicInteger(0);
        volatile long windowStartEpochMs = Instant.now().toEpochMilli();
    }

    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int maxRequestsPerMinute;
    private static final long WINDOW_MS = 60_000L;

    public RateLimiter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    /**
     * Check whether the given key is within its rate limit.
     *
     * @param key e.g. principalId, IP address
     * @return true if the request is allowed, false if the limit is exceeded
     */
    public boolean isAllowed(String key) {
        Window w = windows.computeIfAbsent(key, k -> new Window());
        long now = Instant.now().toEpochMilli();
        synchronized (w) {
            if (now - w.windowStartEpochMs >= WINDOW_MS) {
                w.windowStartEpochMs = now;
                w.count.set(0);
            }
            return w.count.incrementAndGet() <= maxRequestsPerMinute;
        }
    }

    /** Reset the counter for a specific key (useful in tests). */
    public void reset(String key) {
        windows.remove(key);
    }
}
