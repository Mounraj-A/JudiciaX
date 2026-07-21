package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class MedicalStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.MEDICAL_REPORT;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "MEDICAL_ONTOLOGY_OCR_POLICY";
    }
}
