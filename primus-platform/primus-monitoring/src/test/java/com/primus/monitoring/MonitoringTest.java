package com.primus.monitoring;

import com.primus.monitoring.health.*;
import com.primus.monitoring.metrics.MetricRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringTest {

    @Test
    void all_up_returns_up() {
        HealthAggregator agg = new HealthAggregator();
        agg.register(staticIndicator("db", HealthStatus.UP));
        agg.register(staticIndicator("nas", HealthStatus.UP));
        assertEquals(HealthStatus.UP, agg.overallStatus());
    }

    @Test
    void one_down_returns_degraded() {
        HealthAggregator agg = new HealthAggregator();
        agg.register(staticIndicator("db", HealthStatus.UP));
        agg.register(staticIndicator("nas", HealthStatus.DOWN));
        assertEquals(HealthStatus.DEGRADED, agg.overallStatus());
    }

    @Test
    void all_down_returns_down() {
        HealthAggregator agg = new HealthAggregator();
        agg.register(staticIndicator("db", HealthStatus.DOWN));
        assertEquals(HealthStatus.DOWN, agg.overallStatus());
    }

    @Test
    void empty_aggregator_returns_unknown() {
        assertEquals(HealthStatus.UNKNOWN, new HealthAggregator().overallStatus());
    }

    @Test
    void exception_in_indicator_returns_down() {
        HealthAggregator agg = new HealthAggregator();
        agg.register(new HealthIndicator() {
            @Override public HealthCheckResult check() { throw new RuntimeException("DB unreachable"); }
            @Override public String name() { return "flakey-db"; }
        });
        List<HealthCheckResult> results = agg.checkAll();
        assertEquals(HealthStatus.DOWN, results.get(0).getStatus());
    }

    @Test
    void metric_counter_increments() {
        MetricRegistry registry = new MetricRegistry();
        registry.increment("export.success");
        registry.increment("export.success");
        registry.add("export.duration_ms", 250L);
        assertEquals(2, registry.get("export.success"));
        assertEquals(250L, registry.get("export.duration_ms"));
        assertEquals(0, registry.get("export.failure"));
    }

    private static HealthIndicator staticIndicator(String name, HealthStatus status) {
        return new HealthIndicator() {
            @Override public HealthCheckResult check() {
                return new HealthCheckResult(name, status, status == HealthStatus.UP ? "OK" : "Error", null);
            }
            @Override public String name() { return name; }
        };
    }
}
