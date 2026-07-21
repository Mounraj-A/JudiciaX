package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class EvidenceStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.EVIDENCE || 
               documentType == OCRDocumentType.JUDGMENT || 
               documentType == OCRDocumentType.COURT_ORDER;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "FORENSIC_ACCURACY_OCR_POLICY";
    }
}
