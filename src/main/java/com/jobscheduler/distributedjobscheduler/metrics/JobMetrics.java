package com.jobscheduler.distributedjobscheduler.metrics;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JobMetrics {

    private final AtomicInteger totalExecuted = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public void incrementTotal() {
        totalExecuted.incrementAndGet();
    }

    public void incrementSuccess() {
        successCount.incrementAndGet();
    }

    public void incrementFailure() {
        failureCount.incrementAndGet();
    }

    public int getTotalExecuted() {
        return totalExecuted.get();
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }
}
