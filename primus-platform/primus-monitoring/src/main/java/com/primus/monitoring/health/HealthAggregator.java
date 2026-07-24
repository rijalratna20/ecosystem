package com.primus.monitoring.health;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates all registered {@link HealthIndicator}s into a single platform health view.
 *
 * <p>REST mapping: GET /actuator/health
 */
public class HealthAggregator {

    private final List<HealthIndicator> indicators = new ArrayList<>();

    public synchronized void register(HealthIndicator indicator) {
        indicators.add(indicator);
    }

    /**
     * Run all health checks and return the results.
     *
     * @return results for every registered indicator
     */
    public List<HealthCheckResult> checkAll() {
        return indicators.stream()
                .map(i -> {
                    try {
                        return i.check();
                    } catch (Exception ex) {
                        return HealthCheckResult.down(i.name(), "Exception: " + ex.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Overall platform status: UP if all indicators are UP, DEGRADED if some are down, DOWN if all are down.
     */
    public HealthStatus overallStatus() {
        List<HealthCheckResult> results = checkAll();
        if (results.isEmpty()) return HealthStatus.UNKNOWN;
        long upCount = results.stream().filter(r -> r.getStatus() == HealthStatus.UP).count();
        if (upCount == results.size()) return HealthStatus.UP;
        if (upCount == 0) return HealthStatus.DOWN;
        return HealthStatus.DEGRADED;
    }
}
