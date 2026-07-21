package com.courtai.ai.ocr.monitor.impl;

import com.courtai.ai.ocr.dto.OCRMonitoringResponse;
import com.courtai.ai.ocr.monitor.OCRMonitoringService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class OCRMonitoringServiceImpl implements OCRMonitoringService {

    private final AtomicLong activeRequests = new AtomicLong(0);
    private final AtomicLong totalRetries = new AtomicLong(0);
    private final AtomicLong totalFailures = new AtomicLong(0);
    private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
    private final AtomicLong completedRequests = new AtomicLong(0);

    @Override
    public OCRMonitoringResponse getMonitoringMetrics() {
        long completed = completedRequests.get();
        double avgTime = completed > 0 ? (double) totalProcessingTimeMs.get() / completed : 0.0;
        
        return OCRMonitoringResponse.builder()
                .activeRequests(activeRequests.get())
                .queueLength(activeRequests.get()) // Simplified
                .totalRetries(totalRetries.get())
                .totalFailures(totalFailures.get())
                .averageProcessingTimeMs(avgTime)
                .expectedSystemAccuracy(0.95)
                .currentStatus("ONLINE")
                .health("GREEN")
                .build();
    }

    @Override
    public void recordSuccess(long processingTimeMs) {
        completedRequests.incrementAndGet();
        totalProcessingTimeMs.addAndGet(processingTimeMs);
    }

    @Override
    public void recordFailure(boolean isRetry) {
        if (isRetry) {
            totalRetries.incrementAndGet();
        } else {
            totalFailures.incrementAndGet();
        }
    }
}
