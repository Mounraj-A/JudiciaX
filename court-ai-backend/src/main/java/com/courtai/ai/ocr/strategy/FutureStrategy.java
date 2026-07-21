package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class FutureStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.FUTURE_AUDIO || 
               documentType == OCRDocumentType.FUTURE_VIDEO;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "DEFER_TO_FUTURE_PIPELINE";
    }
}
