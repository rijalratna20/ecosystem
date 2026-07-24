package com.primus.scheduler.domain;

import java.time.Instant;

/**
 * Descriptor of a recurring or one-shot scheduled job.
 *
 * <p>Example YAML configuration:
 * <pre>
 * primus.scheduler.jobs:
 *   - name: daily_customer_export
 *     app: CustomerService
 *     schedule: "0 2 * * *"     # 2 AM daily (cron)
 *     action: EXPORT
 *   - name: cleanup_old_exports
 *     schedule: "0 3 * * 0"    # 3 AM Sundays
 *     action: CLEANUP
 *     olderThanDays: 90
 * </pre>
 */
public final class ScheduledJob {

    private final String id;
    private final String name;
    private final String applicationId;
    private final String cronExpression;
    private final String action;
    private JobStatus status;
    private Instant lastRunAt;
    private Instant nextRunAt;
    private String lastError;

    public ScheduledJob(String id, String name, String applicationId,
                        String cronExpression, String action) {
        this.id = id;
        this.name = name;
        this.applicationId = applicationId;
        this.cronExpression = cronExpression;
        this.action = action;
        this.status = JobStatus.SCHEDULED;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getApplicationId() { return applicationId; }
    public String getCronExpression() { return cronExpression; }
    public String getAction() { return action; }
    public JobStatus getStatus() { return status; }
    public Instant getLastRunAt() { return lastRunAt; }
    public Instant getNextRunAt() { return nextRunAt; }
    public String getLastError() { return lastError; }

    public void markRunning() { this.status = JobStatus.RUNNING; this.lastRunAt = Instant.now(); }
    public void markCompleted(Instant nextRun) { this.status = JobStatus.SCHEDULED; this.nextRunAt = nextRun; }
    public void markFailed(String error) { this.status = JobStatus.FAILED; this.lastError = error; }
    public void disable() { this.status = JobStatus.DISABLED; }

    @Override
    public String toString() {
        return "ScheduledJob{id='" + id + "', name='" + name + "', cron='" + cronExpression + "', status=" + status + '}';
    }
}
