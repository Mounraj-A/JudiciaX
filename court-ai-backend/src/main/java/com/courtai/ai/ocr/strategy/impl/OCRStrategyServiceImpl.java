package com.courtai.ai.ocr.strategy.impl;

import com.courtai.ai.ocr.dto.OCRProfileResponse;
import com.courtai.ai.ocr.model.OCRDocumentType;
import com.courtai.ai.ocr.strategy.OCRStrategy;
import com.courtai.ai.ocr.strategy.OCRStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRStrategyServiceImpl implements OCRStrategyService {

    private final List<OCRStrategy> strategies;

    @Override
    public OCRStrategy resolveStrategy(OCRDocumentType documentType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(documentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No OCR Strategy found for document type: " + documentType));
    }

    @Override
    public String determinePolicy(OCRProfileResponse profile) {
        if (profile == null || profile.getDocumentType() == null) {
            return "SKIP_OCR_POLICY";
        }
        OCRStrategy strategy = resolveStrategy(profile.getDocumentType());
        log.debug("Resolved strategy {} for document type {}", strategy.getClass().getSimpleName(), profile.getDocumentType());
        return strategy.determineProcessingPolicy(profile);
    }
}
