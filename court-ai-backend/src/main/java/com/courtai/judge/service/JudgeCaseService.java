package com.courtai.judge.service;

import com.courtai.common.enums.CaseStatus;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.JudgeCaseSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for the judge's case review operations.
 * All operations are scoped to cases assigned to the current judge.
 */
public interface JudgeCaseService {

    /** Returns all cases assigned to the current judge. */
    Page<JudgeCaseSummaryResponse> getAssignedCases(Pageable pageable);

    /**
     * Returns the full detail of a case.
     * Validates that the case is assigned to the current judge.
     */
    JudgeCaseResponse getCaseByUuid(String caseUuid);

    /** Searches assigned cases by keyword. */
    Page<JudgeCaseSummaryResponse> searchCases(String keyword, Pageable pageable);

    /** Returns assigned cases filtered by status. */
    Page<JudgeCaseSummaryResponse> getCasesByStatus(CaseStatus status, Pageable pageable);
}
