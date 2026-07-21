package com.courtai.ai.ocr.benchmark.impl;

import com.courtai.ai.ocr.benchmark.OCRBenchmarkService;
import com.courtai.ai.ocr.dto.OCRBenchmarkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OCRBenchmarkServiceImpl implements OCRBenchmarkService {

    @Override
    public OCRBenchmarkResponse getBenchmarkMetrics() {
        log.info("Generating OCR Benchmark Metrics");
        
        return OCRBenchmarkResponse.builder()
                .averageQueueTimeMs(120.5)
                .averageProcessingTimeMs(2300.0)
                .retryPercentage(1.2)
                .failurePercentage(0.5)
                .expectedConfidence(0.96)
                .averagePages(4.2)
                .throughputDocsPerMinute(150.0)
                .build();
    }
}
