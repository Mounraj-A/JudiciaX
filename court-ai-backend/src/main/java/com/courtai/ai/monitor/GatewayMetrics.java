package com.courtai.ai.monitor;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import com.courtai.ai.health.GatewayStatus;

/**
 * Basic memory-based metrics store.
 * Prepared for future micrometer integration.
 */
@Data
@Component
public class GatewayMetrics {
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong retryCount = new AtomicLong(0);
    private final AtomicLong timeoutCount = new AtomicLong(0);
    
    private final AtomicLong totalLatencyMs = new AtomicLong(0);
    private final AtomicLong minLatencyMs = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxLatencyMs = new AtomicLong(0);
    
    // Future placeholders for percentiles
    private double p95LatencyMs = 0.0;
    private double p99LatencyMs = 0.0;
    
    private final AtomicLong activeRequests = new AtomicLong(0);
    private final AtomicLong queueLength = new AtomicLong(0);
    
    private final long startTime = System.currentTimeMillis();
    private final AtomicReference<GatewayStatus> currentStatus = new AtomicReference<>(GatewayStatus.INITIALIZING);

    public void recordSuccess(long latency) {
        totalRequests.incrementAndGet();
        successfulRequests.incrementAndGet();
        updateLatency(latency);
    }

    public void recordFailure(long latency) {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
        updateLatency(latency);
    }

    public void recordRetry() {
        retryCount.incrementAndGet();
    }

    public void recordTimeout() {
        timeoutCount.incrementAndGet();
        failedRequests.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    private void updateLatency(long latency) {
        totalLatencyMs.addAndGet(latency);
        
        long currentMin;
        do {
            currentMin = minLatencyMs.get();
            if (latency >= currentMin) break;
        } while (!minLatencyMs.compareAndSet(currentMin, latency));

        long currentMax;
        do {
            currentMax = maxLatencyMs.get();
            if (latency <= currentMax) break;
        } while (!maxLatencyMs.compareAndSet(currentMax, latency));
    }

    public double getAverageLatency() {
        long total = totalRequests.get();
        if (total == 0) return 0.0;
        return (double) totalLatencyMs.get() / total;
    }

    public long getUptimeMs() {
        return System.currentTimeMillis() - startTime;
    }
}
