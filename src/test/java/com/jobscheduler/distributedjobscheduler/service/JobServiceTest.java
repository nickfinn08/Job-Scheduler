package com.jobscheduler.distributedjobscheduler.service;

import com.jobscheduler.distributedjobscheduler.enums.JobStatus;
import com.jobscheduler.distributedjobscheduler.model.Job;
import com.jobscheduler.distributedjobscheduler.repository.JobRepository;
import com.jobscheduler.distributedjobscheduler.handler.JobHandlerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JobServiceTest {

    private JobService jobService;
    private JobRepository jobRepository;

    @BeforeEach
    void setup() {
        jobRepository = Mockito.mock(JobRepository.class);
        JobHandlerRegistry registry = Mockito.mock(JobHandlerRegistry.class);
        jobService = new JobService(jobRepository, registry);
    }

    @Test
    void shouldRetryUntilMaxRetriesThenDead() {
        Job job = new Job();
        job.setRetryCount(0);
        job.setMaxRetries(2);
        job.setStatus(JobStatus.RUNNING);

        // first failure
        jobService.markJobFailed(job);
        assertEquals(JobStatus.RETRYING, job.getStatus());

        // second failure
        jobService.markJobFailed(job);
        assertEquals(JobStatus.RETRYING, job.getStatus());

        // third failure
        jobService.markJobFailed(job);
        assertEquals(JobStatus.DEAD, job.getStatus());
    }
    @Test
    void shouldClaimJobWhenRepositoryUpdatesOneRow() {
        Job job = new Job();
        job.setId(java.util.UUID.randomUUID());

        Mockito.when(
                jobRepository.claimJob(
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(1);

        boolean claimed = jobService.claimJob(job, "worker-1");

        assertTrue(claimed);
    }

    @Test
    void shouldNotClaimJobWhenRepositoryUpdatesZeroRows() {
        Job job = new Job();
        job.setId(java.util.UUID.randomUUID());

        Mockito.when(
                jobRepository.claimJob(
                        Mockito.any(),
                        Mockito.any()
                )
        ).thenReturn(0);

        boolean claimed = jobService.claimJob(job, "worker-2");

        assertFalse(claimed);
    }
    @Test
    void shouldRecoverStuckRunningJobs() {
        Job job = new Job();
        job.setStatus(JobStatus.RUNNING);
        job.setRetryCount(0);
        job.setMaxRetries(2);
        job.setLockedAt(LocalDateTime.now().minusMinutes(5));

        Mockito.when(
                jobRepository.findByStatusAndLockedAtBefore(
                        Mockito.eq(JobStatus.RUNNING),
                        Mockito.any()
                )
        ).thenReturn(List.of(job));

        jobService.recoverStuckJobs();

        assertEquals(JobStatus.RETRYING, job.getStatus());
        assertEquals(1, job.getRetryCount());
        assertNull(job.getLockedAt());
        assertNull(job.getLockedBy());
    }


}
