package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRBenchmarkResponse {
    private Double averageQueueTimeMs;
    private Double averageProcessingTimeMs;
    private Double retryPercentage;
    private Double failurePercentage;
    private Double expectedConfidence;
    private Double averagePages;
    private Double throughputDocsPerMinute;
}
