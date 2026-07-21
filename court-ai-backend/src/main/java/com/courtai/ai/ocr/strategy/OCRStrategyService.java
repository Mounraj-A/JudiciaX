package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;

public interface OCRStrategyService {
    
    /**
     * Resolves the appropriate OCR strategy for a given document type.
     */
    OCRStrategy resolveStrategy(OCRDocumentType documentType);
    
    /**
     * Determines the overarching processing policy using the resolved strategy.
     */
    String determinePolicy(OCRProfileResponse profile);
}
