package com.courtai.judge.service.impl;

import com.courtai.ai.entity.CaseAnalysis;
import com.courtai.ai.repository.CaseAnalysisRepository;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeAiAnalysisResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeAiMapper;
import com.courtai.judge.service.JudgeAiReviewService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link JudgeAiReviewService}.
 * Read-only access — no modifications permitted.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeAiReviewServiceImpl implements JudgeAiReviewService {

    private final JudgeService           judgeService;
    private final CaseFileRepository     caseFileRepository;
    private final CaseAnalysisRepository analysisRepository;
    private final JudgeAiMapper          aiMapper;

    @Override
    public JudgeAiAnalysisResponse getAiAnalysis(String caseUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        // Validate judge ownership
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("Case is not assigned to you: " + caseUuid);
        }

        CaseAnalysis analysis = analysisRepository.findByCaseFileUuid(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI analysis not yet available for case: " + caseUuid));

        // Map base fields
        JudgeAiAnalysisResponse response = aiMapper.toResponse(analysis);

        // Manually populate fields not handled by MapStruct
        // XAI factors extracted from rawResponse JSON if available
        String xaiFactors = extractXaiFactors(analysis.getRawResponse());

        return JudgeAiAnalysisResponse.builder()
                .uuid(response.getUuid())
                .caseUuid(response.getCaseUuid())
                .urgencyScore(response.getUrgencyScore())
                .delayImpactScore(response.getDelayImpactScore())
                .priorityScore(response.getPriorityScore())
                .trustScore(response.getTrustScore())
                .confidenceScore(response.getConfidenceScore())
                .recommendation(response.getRecommendation())
                .modelVersion(response.getModelVersion())
                .generatedAt(response.getGeneratedAt())
                .explainableAiFactors(xaiFactors)
                .processingTimeMs(null)   // populated by AI microservice when available
                .build();
    }

    /**
     * Attempts to extract an "xai_factors" or "explanation" block from the raw JSON response.
     * Returns null if not present — no hard dependency on raw response format.
     */
    private String extractXaiFactors(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) return null;
        // Simple substring extraction; full JSON parsing can be added via Jackson if needed
        int idx = rawResponse.indexOf("\"xai_factors\"");
        if (idx < 0) idx = rawResponse.indexOf("\"explanation\"");
        if (idx < 0) return null;
        int start = rawResponse.indexOf(':', idx) + 1;
        // Return a bounded slice to avoid leaking extremely large payloads
        int end = Math.min(start + 2000, rawResponse.length());
        return rawResponse.substring(start, end).trim();
    }
}
