package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class HandwrittenStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.HANDWRITTEN_IMAGE;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "HANDWRITING_RECOGNITION_POLICY";
    }
}
