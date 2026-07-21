package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class ImageStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.IMAGE;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "STANDARD_IMAGE_OCR_POLICY";
    }
}
