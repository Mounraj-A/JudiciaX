package com.courtai.ai.ocr.monitor;

import com.courtai.ai.ocr.dto.OCRMonitoringResponse;

public interface OCRMonitoringService {
    
    /**
     * Retrieves the current OCR queue and system health telemetry.
     */
    OCRMonitoringResponse getMonitoringMetrics();
    
    /**
     * Records a successful OCR operation.
     */
    void recordSuccess(long processingTimeMs);
    
    /**
     * Records an OCR operation failure or retry.
     */
    void recordFailure(boolean isRetry);
}
