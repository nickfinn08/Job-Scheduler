package com.jobscheduler.distributedjobscheduler.service;
import java.time.LocalDateTime;
import java.util.*;
import com.jobscheduler.distributedjobscheduler.dto.CreateJobRequest;
import com.jobscheduler.distributedjobscheduler.enums.JobStatus;
import com.jobscheduler.distributedjobscheduler.model.Job;
import com.jobscheduler.distributedjobscheduler.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import com.jobscheduler.distributedjobscheduler.handler.JobHandlerRegistry;
import com.jobscheduler.distributedjobscheduler.handler.JobHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.jobscheduler.distributedjobscheduler.enums.JobStatus;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobHandlerRegistry jobHandlerRegistry;

    public JobService(JobRepository jobRepository,
                      JobHandlerRegistry jobHandlerRegistry) {
        this.jobRepository = jobRepository;
        this.jobHandlerRegistry = jobHandlerRegistry;
    }

    public Job createJob(CreateJobRequest request) {
        Job job = new Job();
        job.setJobType(request.getJobType());
        job.setPayload(request.getPayload());
        job.setScheduledAt(request.getScheduledAt());
        job.setPriority(request.getPriority());
        job.setMaxRetries(request.getMaxRetries());
        job.setRetryCount(0);
        job.setStatus(JobStatus.SCHEDULED);

        return jobRepository.save(job);
    }
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<Job> findDueJobs() {
        return jobRepository.findByStatusAndScheduledAtLessThanEqual(
                JobStatus.SCHEDULED,
                LocalDateTime.now()
        );
    }
    public boolean claimJob(Job job, String workerId) {
        int updatedRows = jobRepository.claimJob(job.getId(), workerId);
        return updatedRows == 1;
    }
    public void executeJob(Job job) {
        JobHandler handler = jobHandlerRegistry.getHandler(job.getJobType());

        if (handler == null) {
            throw new RuntimeException(
                    "No handler found for job type: " + job.getJobType()
            );
        }

        handler.execute(job);
    }

    @Transactional
    public void markJobCompleted(Job job) {
        job.setStatus(JobStatus.COMPLETED);
        jobRepository.save(job);
    }
    @Transactional
    public void markJobFailed(Job job) {
        int retries = job.getRetryCount() + 1;
        job.setRetryCount(retries);

        if (retries <= job.getMaxRetries()) {
            job.setStatus(JobStatus.RETRYING);
            job.setScheduledAt(LocalDateTime.now().plusSeconds(10));
        } else {
            job.setStatus(JobStatus.DEAD);
        }

        jobRepository.save(job);
    }

    public Page<Job> getJobsPaged(int page, int size) {
        return jobRepository.findAll(PageRequest.of(page, size));
    }
    public Page<Job> getJobsPaged(int page, int size, JobStatus status) {
        PageRequest pageable = PageRequest.of(page, size);

        if (status == null) {
            return jobRepository.findAll(pageable);
        }

        return jobRepository.findByStatus(status, pageable);
    }
    @Transactional
    public void recoverStuckJobs() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2);

        List<Job> stuckJobs = jobRepository.findByStatusAndLockedAtBefore(
                JobStatus.RUNNING,
                cutoffTime
        );

        for (Job job : stuckJobs) {
            int retries = job.getRetryCount() + 1;
            job.setRetryCount(retries);

            if (retries <= job.getMaxRetries()) {
                job.setStatus(JobStatus.RETRYING);
                job.setScheduledAt(LocalDateTime.now().plusSeconds(10));
                job.setLockedAt(null);
                job.setLockedBy(null);
            } else {
                job.setStatus(JobStatus.DEAD);
            }

            jobRepository.save(job);
        }
    }

}
