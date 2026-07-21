package com.courtai.ai.ocr.strategy;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import org.springframework.stereotype.Component;

@Component
public class DigitalPDFStrategy implements OCRStrategy {
    @Override
    public boolean supports(OCRDocumentType documentType) {
        return documentType == OCRDocumentType.DIGITAL_PDF;
    }

    @Override
    public String determineProcessingPolicy(OCRProfileResponse profile) {
        return "NATIVE_PDF_PARSER_POLICY";
    }
}
