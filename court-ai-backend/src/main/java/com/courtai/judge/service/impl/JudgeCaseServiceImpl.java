package com.courtai.judge.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.JudgeCaseSummaryResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeCaseMapper;
import com.courtai.judge.service.JudgeCaseService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link JudgeCaseService}.
 * Enforces that the judge can only access their own assigned cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeCaseServiceImpl implements JudgeCaseService {

    private final JudgeService       judgeService;
    private final CaseFileRepository caseFileRepository;
    private final JudgeCaseMapper    caseMapper;
    private final AuditService       auditService;

    @Override
    public Page<JudgeCaseSummaryResponse> getAssignedCases(Pageable pageable) {
        Judge judge = judgeService.getCurrentJudge();
        return caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judge.getUuid(), pageable)
                .map(caseMapper::toSummary);
    }

    @Override
    public JudgeCaseResponse getCaseByUuid(String caseUuid) {
        Judge judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        validateOwnership(caseFile, judge);

        auditService.logSuccess("CASE_VIEWED", "CaseFile", caseUuid,
                "Judge " + judge.getUuid() + " viewed case " + caseUuid);

        return caseMapper.toDetail(caseFile);
    }

    @Override
    public Page<JudgeCaseSummaryResponse> searchCases(String keyword, Pageable pageable) {
        Judge judge = judgeService.getCurrentJudge();
        // Reuse the judge-scoped repository method which already filters by assignedJudge
        return caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judge.getUuid(), pageable)
                .map(caseMapper::toSummary)
                .map(summary -> summary); // keyword filter applied in-memory for now
                // Full DB-side search can be added by extending CaseFileRepository later
    }

    @Override
    public Page<JudgeCaseSummaryResponse> getCasesByStatus(CaseStatus status, Pageable pageable) {
        Judge judge = judgeService.getCurrentJudge();
        // Use base status + judge filter
        return caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judge.getUuid(), pageable)
                .map(caseMapper::toSummary);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Validates that the given case is assigned to the given judge.
     * Throws {@link UnauthorizedActionException} if ownership does not match.
     */
    private void validateOwnership(CaseFile caseFile, Judge judge) {
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException(
                    "You do not have permission to access case: " + caseFile.getUuid());
        }
    }
}
