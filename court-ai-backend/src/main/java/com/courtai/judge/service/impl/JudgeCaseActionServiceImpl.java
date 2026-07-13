package com.courtai.judge.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.DisposeRequest;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.ReserveJudgmentRequest;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeCaseMapper;
import com.courtai.judge.service.JudgeCaseActionService;
import com.courtai.judge.service.JudgeNotificationService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * Implementation of {@link JudgeCaseActionService}.
 * Enforces the judicial status-transition state machine.
 *
 * <p>Allowed transitions driven by judge actions:</p>
 * <pre>
 * REGISTERED / JUDGE_ASSIGNED / HEARING_SCHEDULED / IN_PROGRESS / ADJOURNED
 *   → JUDGEMENT_RESERVED  (via reserveJudgment)
 *
 * JUDGEMENT_RESERVED
 *   → DISPOSED             (via disposeCase)
 *
 * DISPOSED
 *   → IN_PROGRESS          (via reopenCase — restricted business scenario)
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class JudgeCaseActionServiceImpl implements JudgeCaseActionService {

    /** Statuses from which a judge may reserve judgment. */
    private static final Set<CaseStatus> RESERVE_ALLOWED_FROM = EnumSet.of(
            CaseStatus.JUDGE_ASSIGNED,
            CaseStatus.HEARING_SCHEDULED,
            CaseStatus.IN_HEARING,
            CaseStatus.IN_PROGRESS,
            CaseStatus.ADJOURNED,
            CaseStatus.ADMITTED
    );

    private final JudgeService                  judgeService;
    private final CaseFileRepository            caseFileRepository;
    private final CaseStatusHistoryRepository   statusHistoryRepository;
    private final JudgeCaseMapper               caseMapper;
    private final AuditService                  auditService;
    private final JudgeNotificationService      notificationService;

    @Override
    @Transactional
    public JudgeCaseResponse reserveJudgment(String caseUuid, ReserveJudgmentRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        if (!RESERVE_ALLOWED_FROM.contains(caseFile.getStatus())) {
            throw new BusinessRuleViolationException(
                    "Cannot reserve judgment from current status: " + caseFile.getStatus()
                            + ". Case must be in hearing or in progress.");
        }

        transitionStatus(caseFile, CaseStatus.JUDGEMENT_RESERVED, judge.getUuid(),
                "Judgment reserved" + (request.getReason() != null
                        ? ": " + request.getReason() : "."));

        auditService.logSuccess("JUDGMENT_RESERVED", "CaseFile", caseUuid,
                "Judge " + judge.getUuid() + " reserved judgment on case " + caseUuid);

        notificationService.notifyJudgmentReserved(caseFile);

        return caseMapper.toDetail(caseFile);
    }

    @Override
    @Transactional
    public JudgeCaseResponse disposeCase(String caseUuid, DisposeRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        // Disposal allowed from JUDGEMENT_RESERVED, or IN_PROGRESS (direct disposal)
        if (caseFile.getStatus() != CaseStatus.JUDGEMENT_RESERVED
                && caseFile.getStatus() != CaseStatus.IN_PROGRESS) {
            throw new BusinessRuleViolationException(
                    "Cannot dispose case from status: " + caseFile.getStatus()
                            + ". Judgment must be reserved first.");
        }

        transitionStatus(caseFile, CaseStatus.DISPOSED, judge.getUuid(),
                "Case disposed. Reason: " + request.getDisposalReason());

        auditService.logSuccess("CASE_DISPOSED", "CaseFile", caseUuid,
                "Judge " + judge.getUuid() + " disposed case " + caseUuid);

        notificationService.notifyCaseDisposed(caseFile);

        return caseMapper.toDetail(caseFile);
    }

    @Override
    @Transactional
    public JudgeCaseResponse reopenCase(String caseUuid, String reason) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        // Only DISPOSED cases can be reopened
        if (caseFile.getStatus() != CaseStatus.DISPOSED) {
            throw new BusinessRuleViolationException(
                    "Only disposed cases can be reopened. Current status: " + caseFile.getStatus());
        }

        transitionStatus(caseFile, CaseStatus.IN_PROGRESS, judge.getUuid(),
                "Case reopened. Reason: " + (reason != null ? reason : "No reason provided."));

        auditService.logSuccess("CASE_REOPENED", "CaseFile", caseUuid,
                "Judge " + judge.getUuid() + " reopened case " + caseUuid);

        return caseMapper.toDetail(caseFile);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CaseFile getAssignedCase(String caseUuid, Judge judge) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("Case is not assigned to you: " + caseUuid);
        }
        return caseFile;
    }

    private void transitionStatus(CaseFile caseFile, CaseStatus newStatus,
                                   String actorUuid, String remarks) {
        CaseStatus oldStatus = caseFile.getStatus();
        caseFile.setStatus(newStatus);
        caseFileRepository.save(caseFile);

        CaseStatusHistory history = CaseStatusHistory.builder()
                .caseFile(caseFile)
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .changedByUuid(actorUuid)
                .changedByRole("ROLE_JUDGE")
                .changedAt(LocalDateTime.now())
                .remarks(remarks)
                .build();
        statusHistoryRepository.save(history);
    }
}
