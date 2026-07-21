package com.courtai.ai.ocr.routing.impl;

import com.courtai.ai.ocr.dto.OCRDecisionResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.routing.OCRRoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OCRRoutingServiceImpl implements OCRRoutingService {

    @Override
    public String determineRoute(OCRProfileResponse profile, OCRDecisionResponse decision) {
        log.info("Determining OCR route for document UUID: {}", profile.getDocumentUuid());

        return switch (decision.getDecision()) {
            case "RUN_OCR" -> "OCR_PIPELINE";
            case "SKIP_OCR" -> "BYPASS_OCR";
            case "RETRY_OCR" -> "RETRY_QUEUE";
            case "MANUAL_REVIEW" -> "MANUAL_REVIEW_QUEUE";
            case "FUTURE_SPEECH" -> "SPEECH_PIPELINE";
            case "FUTURE_VIDEO" -> "VIDEO_PIPELINE";
            default -> "REJECTED";
        };
    }
}
