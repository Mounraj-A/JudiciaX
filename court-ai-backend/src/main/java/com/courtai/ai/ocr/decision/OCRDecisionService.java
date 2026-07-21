package com.courtai.ai.ocr.decision;

import com.courtai.ai.ocr.dto.OCRDecisionResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRQualityResponse;

public interface OCRDecisionService {
    
    /**
     * Determines the execution decision (e.g., RUN_OCR, SKIP_OCR, MANUAL_REVIEW)
     * based on the profile, quality, and resolved strategy policy.
     */
    OCRDecisionResponse determineDecision(OCRProfileResponse profile, OCRQualityResponse quality, String strategyPolicy);
}
