package com.jobscheduler.distributedjobscheduler.handler;

import com.jobscheduler.distributedjobscheduler.model.Job;
import org.springframework.stereotype.Component;

@Component
public class EmailJobHandler implements JobHandler {

    @Override
    public String getJobType() {
        return "EMAIL";
    }

    @Override
    public void execute(Job job) {
        System.out.println(
                "Sending EMAIL for job " + job.getId()
                        + " | payload=" + job.getPayload()
        );
    }
}
