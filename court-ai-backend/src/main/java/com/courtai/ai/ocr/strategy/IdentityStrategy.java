package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class IdentityStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.IDENTITY_DOCUMENT;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "ID_EXTRACTION_POLICY";
    }
}
