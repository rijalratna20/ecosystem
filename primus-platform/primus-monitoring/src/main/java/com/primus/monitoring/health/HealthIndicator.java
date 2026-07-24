package com.primus.monitoring.health;

/**
 * SPI for components that contribute a health status.
 * Implement this interface in each module (database, storage, cache)
 * and register with {@link HealthAggregator}.
 */
public interface HealthIndicator {
    /**
     * Perform the health check and return the result.
     * Implementations must be idempotent and fast (&lt; 500 ms).
     */
    HealthCheckResult check();

    /** Human-readable name for this indicator (e.g. "database", "nas-storage"). */
    String name();
}
