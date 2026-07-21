package com.courtai.ai.ocr.dataset;

import com.courtai.ai.ocr.dto.OCRDatasetResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRValidationResponse;

public interface OCRDatasetService {
    
    /**
     * Collects and structures anonymized telemetry for OCR dataset research,
     * without storing raw document text.
     */
    OCRDatasetResponse recordDatasetEntry(OCRProfileResponse profile, OCRValidationResponse validation, long processingTimeMs);
}
