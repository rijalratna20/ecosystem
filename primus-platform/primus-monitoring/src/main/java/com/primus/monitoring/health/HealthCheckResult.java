package com.primus.monitoring.health;

import java.time.Instant;
import java.util.Map;

/** The result of evaluating a single health check. */
public final class HealthCheckResult {

    private final String name;
    private final HealthStatus status;
    private final String message;
    private final Instant checkedAt;
    private final Map<String, Object> details;

    public HealthCheckResult(String name, HealthStatus status, String message,
                             Map<String, Object> details) {
        this.name = name;
        this.status = status;
        this.message = message;
        this.checkedAt = Instant.now();
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public String getName() { return name; }
    public HealthStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public Instant getCheckedAt() { return checkedAt; }
    public Map<String, Object> getDetails() { return details; }

    public static HealthCheckResult up(String name) {
        return new HealthCheckResult(name, HealthStatus.UP, "OK", null);
    }

    public static HealthCheckResult down(String name, String reason) {
        return new HealthCheckResult(name, HealthStatus.DOWN, reason, null);
    }

    @Override
    public String toString() {
        return "HealthCheckResult{name='" + name + "', status=" + status + ", message='" + message + "'}";
    }
}
