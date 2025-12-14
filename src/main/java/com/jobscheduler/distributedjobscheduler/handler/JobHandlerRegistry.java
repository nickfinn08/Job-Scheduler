package com.jobscheduler.distributedjobscheduler.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobHandlerRegistry {

    private final Map<String, JobHandler> handlers = new HashMap<>();

    public JobHandlerRegistry(List<JobHandler> jobHandlers) {
        for (JobHandler handler : jobHandlers) {
            handlers.put(handler.getJobType(), handler);
        }
    }

    public JobHandler getHandler(String jobType) {
        return handlers.get(jobType);
    }
}
