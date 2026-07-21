package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRMonitoringResponse {
    private Long activeRequests;
    private Long queueLength;
    private Long totalRetries;
    private Long totalFailures;
    private Double averageProcessingTimeMs;
    private Double expectedSystemAccuracy;
    private String currentStatus;
    private String health;
}
