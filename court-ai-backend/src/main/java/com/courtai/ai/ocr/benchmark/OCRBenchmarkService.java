package com.courtai.ai.ocr.benchmark;

import com.courtai.ai.ocr.dto.OCRBenchmarkResponse;

public interface OCRBenchmarkService {
    
    /**
     * Retrieves OCR benchmark metrics for research and paper evaluation.
     */
    OCRBenchmarkResponse getBenchmarkMetrics();
}
