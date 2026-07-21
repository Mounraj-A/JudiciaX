package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;

public interface OCRStrategy {
    boolean supports(OCRDocumentType documentType);
    String determineProcessingPolicy(OCRProfileResponse profile);
}
