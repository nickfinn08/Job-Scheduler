package com.jobscheduler.distributedjobscheduler.handler;

import com.jobscheduler.distributedjobscheduler.model.Job;

public interface JobHandler {

    /**
     * @return job type this handler supports (e.g. EMAIL, REPORT)
     */
    String getJobType();

    /**
     * Execute the job
     */
    void execute(Job job);
}
