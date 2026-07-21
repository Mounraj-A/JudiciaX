package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class ScannedPDFStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.SCANNED_PDF;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "HIGH_ACCURACY_OCR_POLICY";
    }
}
