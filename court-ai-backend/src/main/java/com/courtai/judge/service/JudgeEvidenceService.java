package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeEvidenceResponse;

import java.util.List;

/**
 * Service contract for the judge's read-only evidence review.
 */
public interface JudgeEvidenceService {

    /** Returns all evidence for a case assigned to the current judge. */
    List<JudgeEvidenceResponse> getEvidenceByCase(String caseUuid);

    /** Returns a specific evidence item — validates judge is assigned to the case. */
    JudgeEvidenceResponse getEvidenceByUuid(String caseUuid, String evidenceUuid);
}
