package com.primus.scheduler;

import com.primus.scheduler.domain.JobStatus;
import com.primus.scheduler.domain.ScheduledJob;
import com.primus.scheduler.service.InMemoryJobScheduler;
import com.primus.scheduler.service.JobScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobSchedulerTest {

    private JobScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new InMemoryJobScheduler();
    }

    private ScheduledJob job(String id, String name) {
        return new ScheduledJob(id, name, "app-1", "0 2 * * *", "EXPORT");
    }

    @Test
    void register_and_find() {
        scheduler.register(job("j1", "daily-export"));
        assertNotNull(scheduler.findById("j1"));
        assertEquals("daily-export", scheduler.findById("j1").getName());
    }

    @Test
    void trigger_now_marks_completed() {
        scheduler.register(job("j2", "on-demand"));
        scheduler.triggerNow("j2");
        assertEquals(JobStatus.SCHEDULED, scheduler.findById("j2").getStatus());
    }

    @Test
    void disable_prevents_trigger() {
        scheduler.register(job("j3", "blocked"));
        scheduler.disable("j3");
        assertThrows(IllegalStateException.class, () -> scheduler.triggerNow("j3"));
    }

    @Test
    void list_by_status() {
        scheduler.register(job("j4", "a"));
        scheduler.register(job("j5", "b"));
        scheduler.disable("j5");
        List<ScheduledJob> scheduled = scheduler.list(JobStatus.SCHEDULED);
        assertEquals(1, scheduled.size());
        assertEquals("j4", scheduled.get(0).getId());
    }

    @Test
    void remove_job() {
        scheduler.register(job("j6", "temp"));
        assertTrue(scheduler.remove("j6"));
        assertNull(scheduler.findById("j6"));
    }

    @Test
    void register_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> scheduler.register(null));
    }
}
