package com.courtai.ai.ocr.decision.impl;

import com.courtai.ai.ocr.decision.OCRDecisionService;
import com.courtai.ai.ocr.dto.OCRDecisionResponse;
import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.dto.OCRQualityResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ocrEngineDecisionServiceImpl")
public class OCRDecisionServiceImpl implements OCRDecisionService {

    @Override
    public OCRDecisionResponse determineDecision(OCRProfileResponse profile, OCRQualityResponse quality, String strategyPolicy) {
        log.info("Determining OCR decision for document UUID: {}", profile.getDocumentUuid());

        String decision;
        String reason;

        if (profile.getDocumentType() == OCRDocumentType.UNKNOWN) {
            decision = "SKIP_OCR";
            reason = "Document type is unknown. No OCR possible.";
        } else if (profile.getDocumentType() == OCRDocumentType.FUTURE_AUDIO || profile.getDocumentType() == OCRDocumentType.FUTURE_VIDEO) {
            decision = profile.getDocumentType() == OCRDocumentType.FUTURE_AUDIO ? "FUTURE_SPEECH" : "FUTURE_VIDEO";
            reason = "Deferred to future media processing pipelines.";
        } else if (Boolean.TRUE.equals(profile.getRequiresManualReview()) || quality.getReadinessScore() < 0.5) {
            decision = "MANUAL_REVIEW";
            reason = "Complex document or low quality score requires manual verification before OCR.";
        } else {
            decision = "RUN_OCR";
            reason = "Document passes intelligence thresholds for automated processing.";
        }

        return OCRDecisionResponse.builder()
                .decision(decision)
                .reason(reason)
                .strategyToExecute(strategyPolicy)
                .build();
    }
}
