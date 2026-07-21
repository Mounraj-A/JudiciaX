package com.courtai.ai.ocr.quality;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRQualityResponse;

public interface OCRQualityService {
    
    /**
     * Assesses expected resolution, brightness, and noise placeholders for the document.
     */
    OCRQualityResponse assessQuality(OCRProfileResponse profile);
}
