package com.courtai.ai.ocr.profile.impl;

import com.courtai.ai.ocr.dto.OCRProfileRequest;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.model.OCREngineType;
import com.courtai.ai.ocr.profile.OCRProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OCRProfileServiceImpl implements OCRProfileService {

    @Override
    public OCRProfileResponse generateProfile(OCRProfileRequest request) {
        log.info("Generating OCR Profile for document UUID: {}", request.getDocumentUuid());

        // In a real implementation, this would interact with Document Intelligence layer
        // For Phase 4 orchestration architecture, we build a deterministic profile simulation
        String filename = request.getFile() != null ? request.getFile().getOriginalFilename() : "";
        OCRDocumentType type = determineType(filename);
        
        return OCRProfileResponse.builder()
                .documentUuid(request.getDocumentUuid())
                .caseUuid(request.getCaseUuid())
                .documentType(type)
                .language("eng")
                .expectedOcrEngine(determineExpectedEngine(type))
                .expectedAccuracy(0.92)
                .expectedProcessingTimeMs(3000L)
                .requiresRotation(false)
                .requiresEnhancement(type == OCRDocumentType.IMAGE || type == OCRDocumentType.HANDWRITTEN_IMAGE)
                .requiresManualReview(type == OCRDocumentType.HANDWRITTEN_IMAGE)
                .isMultiPage(type == OCRDocumentType.SCANNED_PDF)
                .isMultiColumn(false)
                .isHandwritten(type == OCRDocumentType.HANDWRITTEN_IMAGE)
                .isMixedLanguage(false)
                .estimatedComplexity(type == OCRDocumentType.HANDWRITTEN_IMAGE ? 0.9 : 0.3)
                .ocrReady(true)
                .build();
    }

    private OCRDocumentType determineType(String filename) {
        if (filename == null) return OCRDocumentType.UNKNOWN;
        filename = filename.toLowerCase();
        
        if (filename.contains("scan") && filename.endsWith(".pdf")) return OCRDocumentType.SCANNED_PDF;
        if (filename.endsWith(".pdf")) return OCRDocumentType.DIGITAL_PDF;
        if (filename.contains("handwritten")) return OCRDocumentType.HANDWRITTEN_IMAGE;
        if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return OCRDocumentType.IMAGE;
        
        return OCRDocumentType.UNKNOWN;
    }

    private OCREngineType determineExpectedEngine(OCRDocumentType type) {
        return switch (type) {
            case DIGITAL_PDF -> OCREngineType.NATIVE_PDF_PARSER;
            case HANDWRITTEN_IMAGE -> OCREngineType.AWS_TEXTRACT;
            case IMAGE, SCANNED_PDF -> OCREngineType.TESSERACT;
            case UNKNOWN -> OCREngineType.SKIP;
            default -> OCREngineType.TESSERACT;
        };
    }
}
