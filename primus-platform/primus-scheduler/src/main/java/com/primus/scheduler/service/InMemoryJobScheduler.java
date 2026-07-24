package com.primus.scheduler.service;

import com.primus.scheduler.domain.JobStatus;
import com.primus.scheduler.domain.ScheduledJob;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of {@link JobScheduler}.
 * Does not actually execute cron jobs; execution is handled by the runtime
 * (Spring {@code @Scheduled} or Quartz) calling {@link #triggerNow(String)}.
 */
public class InMemoryJobScheduler implements JobScheduler {

    private final Map<String, ScheduledJob> jobs = new ConcurrentHashMap<>();

    @Override
    public ScheduledJob register(ScheduledJob job) {
        if (job == null) throw new IllegalArgumentException("job must not be null");
        jobs.put(job.getId(), job);
        return job;
    }

    @Override
    public void triggerNow(String jobId) {
        ScheduledJob job = requireFound(jobId);
        if (job.getStatus() == JobStatus.DISABLED) {
            throw new IllegalStateException("Job " + jobId + " is disabled");
        }
        job.markRunning();
        // Real implementation would hand off to an executor/thread-pool here
        job.markCompleted(null);
    }

    @Override
    public void disable(String jobId) {
        requireFound(jobId).disable();
    }

    @Override
    public void enable(String jobId) {
        ScheduledJob job = requireFound(jobId);
        if (job.getStatus() == JobStatus.DISABLED) {
            // reset to SCHEDULED
            jobs.put(job.getId(), new ScheduledJob(
                    job.getId(), job.getName(), job.getApplicationId(),
                    job.getCronExpression(), job.getAction()));
        }
    }

    @Override
    public List<ScheduledJob> list(JobStatus statusFilter) {
        return jobs.values().stream()
                .filter(j -> statusFilter == null || j.getStatus() == statusFilter)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduledJob findById(String jobId) {
        return jobs.get(jobId);
    }

    @Override
    public boolean remove(String jobId) {
        return jobs.remove(jobId) != null;
    }

    private ScheduledJob requireFound(String id) {
        ScheduledJob job = jobs.get(id);
        if (job == null) throw new IllegalArgumentException("Job not found: " + id);
        return job;
    }
}
