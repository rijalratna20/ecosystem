# primus-scheduler

Job scheduler for recurring exports and maintenance tasks.

## Overview

`primus-scheduler` executes recurring exports and maintenance jobs on a schedule, automating routine data operations like daily exports, cleanup jobs, and backup tasks.

## Module Status

🟠 **NOT YET IMPLEMENTED (Tier 8 - Phase 2)**

**Target Launch**: Week 18-22 of Phase 2  
**Phase**: v1.1 (Production Hardening) delivery  
**Priority**: MEDIUM - Automates routine tasks  

## Purpose

Automates recurring operations:
- **Scheduled exports**: Daily/weekly customer data exports
- **Data cleanup**: Delete old exports after retention period
- **Maintenance**: Optimize indices, rebuild caches
- **Backup**: Automated backup jobs

## Key Features (Planned)

### Cron-Based Scheduling
```
0 2 * * * — Daily at 2 AM
0 2 * * 1 — Weekly on Monday at 2  AM
0 2 1 * * — Monthly on 1st at 2 AM
*/15 * * * * — Every 15 minutes
```

### Job States
- `PENDING` — Scheduled, waiting to run
- `RUNNING` — Currently executing
- `COMPLETED` — Successfully finished
- `FAILED` — Execution error
- `RETRYING` — Retry in progress

### Job Management
```
CREATE /api/v1/schedules
{
  "name": "daily_customer_export",
  "application": "CustomerService",
  "schedule": "0 2 * * *",
  "timezone": "America/New_York",
  "filter": "lastModified >= TODAY",
  "maxRetries": 3,
  "retryDelay": "1h"
}

GET /api/v1/schedules
GET /api/v1/schedules/{scheduleId}/history
DELETE /api/v1/schedules/{scheduleId}
```

## Configuration (Planned)

```properties
primus.scheduler.enabled=true

# Queue
primus.scheduler.queue.backend=redis
primus.scheduler.queue.max-workers=10

# Retry
primus.scheduler.retry.max-attempts=3
primus.scheduler.retry.backoff-multiplier=2
primus.scheduler.retry.initial-delay=1m

# Timeout
primus.scheduler.job.timeout=1h
primus.scheduler.job.max-duration=4h

# Persistence
primus.scheduler.persistence.backend=postgresql
```

## Job Types (Planned)

### Export Job
```java
ScheduledJob exportJob = ScheduledJob.builder()
    .type(JobType.EXPORT)
    .application("CustomerService")
    .collections(Arrays.asList("customers", "accounts"))
    .filter("region = 'US'")
    .schedule("0 2 * * *")
    .build();
```

### Cleanup Job
```java
ScheduledJob cleanupJob = ScheduledJob.builder()
    .type(JobType.CLEANUP)
    .olderThanDays(90)
    .schedule("0 3 * * 0")  // Weekly Sunday 3 AM
    .build();
```

### Backup Job
```java
ScheduledJob backupJob = ScheduledJob.builder()
    .type(JobType.BACKUP)
    .destination("s3://primus-backups")
    .schedule("0 4 * * 0")  // Weekly Sunday 4 AM
    .build();
```

## Technical Design (Planned)

### Distributed Scheduler
```
Job Queue (Redis/RabbitMQ)
    ↓
Scheduler Service (distributes jobs)
    ↓
Worker Pool (executes jobs)
    ├─ Worker 1
    ├─ Worker 2
    └─ Worker N
    ↓
Results DB (tracks execution)
```

### Execution Model
- Distributed workers (multiple instances)
- Persistent queue (resilient to failures)
- Automatic retry with backoff
- Job timeout protection
- Graceful degradation

## Monitoring

### Metrics
- `primus_scheduled_jobs_total` — Total jobs executed
- `primus_scheduled_jobs_duration_seconds` — Job duration
- `primus_scheduled_jobs_failed_total` — Failed jobs
- `primus_job_queue_depth` — Pending jobs

### Alerts
- Job timeout: alert if job > 4 hours
- High failure rate: alert if > 10% failing
- Stuck jobs: alert if pending > 2x normal duration

## Testing Strategy

### Unit Tests
- Cron expression parsing
- Retry logic
- Job state machine

### Integration Tests
- End-to-end job execution
- Retry on failure
- Timeout handling
- Distributed execution

### Load Tests
- 1000+ concurrent jobs
- Long-running job handling
- Queue throughput

## Module Dependencies

- `primus-common` — shared types
- `primus-server` — job execution
- `primus-notification` (Tier 4) — job notifications
- Job queue backend (Redis/RabbitMQ)
- Database for persistence

## Deployment Notes

- Requires distributed job queue
- Supports multiple scheduler instances (high availability)
- Set appropriate job timeouts
- Monitor queue depth
- Test failover behavior

## Related Modules

- **primus-server** (Core) — Executes export/retrieval jobs
- **primus-notification** (Tier 4) — Notifies on job completion
- **primus-monitoring** (Tier 6) — Tracks job metrics

---

**Tier**: 8 (Operations - Phase 2)  
**Status**: 🟠 NOT STARTED  
**Priority**: MEDIUM  
**Target Start**: October 8, 2026  
**Target Completion**: October 22, 2026

