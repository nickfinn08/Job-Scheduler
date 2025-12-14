package com.jobscheduler.distributedjobscheduler.scheduler;

import com.jobscheduler.distributedjobscheduler.model.Job;
import com.jobscheduler.distributedjobscheduler.service.JobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.jobscheduler.distributedjobscheduler.metrics.JobMetrics;
import java.util.List;
import java.util.UUID;

@Component
public class JobScheduler {

    private final JobService jobService;
    private final JobMetrics jobMetrics;
    private final String workerId = UUID.randomUUID().toString();

    public JobScheduler(JobService jobService,
                        JobMetrics jobMetrics) {
        this.jobService = jobService;
        this.jobMetrics = jobMetrics;
    }

    @Scheduled(fixedDelay = 5000)
    public void runScheduler() {
        List<Job> dueJobs = jobService.findDueJobs();
        for (Job job : dueJobs) {
            boolean claimed = jobService.claimJob(job, workerId);

            if (claimed) {
                try {
                    jobService.executeJob(job);
                    jobService.markJobCompleted(job);
                    jobMetrics.incrementSuccess();
                }catch(Exception e) {
                    jobService.markJobFailed(job);
                    jobMetrics.incrementFailure();
                }
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStuckJobs() {
        jobService.recoverStuckJobs();
    }

}
