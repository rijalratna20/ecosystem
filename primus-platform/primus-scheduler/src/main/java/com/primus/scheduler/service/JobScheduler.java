package com.primus.scheduler.service;

import com.primus.scheduler.domain.JobStatus;
import com.primus.scheduler.domain.ScheduledJob;

import java.util.List;

/**
 * Manages the lifecycle of scheduled jobs.
 *
 * <p>Implementations may use Quartz, Spring Scheduler, or a custom cron engine.
 */
public interface JobScheduler {

    /** Register a new job. Starts scheduling immediately if status is SCHEDULED. */
    ScheduledJob register(ScheduledJob job);

    /** Trigger a job immediately (out of schedule). */
    void triggerNow(String jobId);

    /** Disable a job without removing it. */
    void disable(String jobId);

    /** Re-enable a previously disabled job. */
    void enable(String jobId);

    /** List all jobs, optionally filtered by status. */
    List<ScheduledJob> list(JobStatus statusFilter);

    /** Find a job by id, or return null if not found. */
    ScheduledJob findById(String jobId);

    /** Remove a job permanently. */
    boolean remove(String jobId);
}
