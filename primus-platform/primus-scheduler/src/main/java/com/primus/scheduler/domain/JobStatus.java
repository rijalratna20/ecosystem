package com.primus.scheduler.domain;

/** Lifecycle state of a scheduled job. */
public enum JobStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    DISABLED
}
