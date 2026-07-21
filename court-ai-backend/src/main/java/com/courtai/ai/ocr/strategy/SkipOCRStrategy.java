package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class SkipOCRStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.UNKNOWN;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "SKIP_OCR_POLICY";
    }
}
