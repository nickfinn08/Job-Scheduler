package com.jobscheduler.distributedjobscheduler.controller;

import com.jobscheduler.distributedjobscheduler.metrics.JobMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    private final JobMetrics jobMetrics;

    public MetricsController(JobMetrics jobMetrics) {
        this.jobMetrics = jobMetrics;
    }

    @GetMapping("/metrics")
    public Map<String, Integer> getMetrics() {
        Map<String, Integer> response = new HashMap<>();
        response.put("totalExecuted", jobMetrics.getTotalExecuted());
        response.put("successCount", jobMetrics.getSuccessCount());
        response.put("failureCount", jobMetrics.getFailureCount());
        return response;
    }
}
