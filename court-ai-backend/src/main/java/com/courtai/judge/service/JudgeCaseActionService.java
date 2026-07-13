package com.courtai.judge.service;

import com.courtai.judge.dto.DisposeRequest;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.ReserveJudgmentRequest;

/**
 * Service contract for terminal judicial actions on a case.
 * Governs the final phase of the judicial lifecycle.
 */
public interface JudgeCaseActionService {

    /**
     * Reserves judgment on a case.
     * Valid preceding statuses: IN_PROGRESS, HEARING_SCHEDULED, IN_HEARING, ADJOURNED.
     * Sets status to JUDGEMENT_RESERVED.
     */
    JudgeCaseResponse reserveJudgment(String caseUuid, ReserveJudgmentRequest request);

    /**
     * Disposes of a case (final closure after judgment).
     * Valid preceding status: JUDGEMENT_RESERVED.
     * Sets status to DISPOSED.
     */
    JudgeCaseResponse disposeCase(String caseUuid, DisposeRequest request);

    /**
     * Reopens a disposed case.
     * Business rule: only permitted when a higher-court remand or clerical error
     * requires re-examination. Sets status back to IN_PROGRESS.
     */
    JudgeCaseResponse reopenCase(String caseUuid, String reason);
}
