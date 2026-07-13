package com.courtai.admin.service.impl;

import com.courtai.admin.dto.JudgeWorkloadResponse;
import com.courtai.admin.dto.AssignJudgeRequest;
import com.courtai.admin.service.JudgeAdministrationService;
import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.repository.JudgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeAdministrationServiceImpl implements JudgeAdministrationService {

    private final JudgeRepository      judgeRepository;
    private final CaseFileRepository   caseFileRepository;
    private final AuditService         auditService;

    @Override
    public Page<JudgeWorkloadResponse> getJudgeWorkloads(Pageable pageable) {
        return judgeRepository.findAll(pageable).map(this::toWorkload);
    }

    @Override
    public JudgeWorkloadResponse getJudgeWorkload(String judgeUserUuid) {
        Judge judge = judgeRepository.findByUserUuidAndIsDeletedFalse(judgeUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Judge", "userUuid", judgeUserUuid));
        return toWorkload(judge);
    }

    @Override
    @Transactional
    public void assignJudgeToCase(AssignJudgeRequest request, String adminUuid) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(request.getCaseUuid())
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", request.getCaseUuid()));
        if (caseFile.getStatus() == CaseStatus.DISPOSED || caseFile.getStatus() == CaseStatus.CLOSED) {
            throw new BusinessRuleViolationException("Cannot assign a judge to a disposed or closed case.");
        }
        Judge judge = judgeRepository.findByUserUuidAndIsDeletedFalse(request.getJudgeUserUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Judge", "userUuid", request.getJudgeUserUuid()));

        caseFile.setAssignedJudge(judge);
        caseFile.setStatus(CaseStatus.JUDGE_ASSIGNED);
        caseFileRepository.save(caseFile);

        auditService.logSuccess("JUDGE_ASSIGNED", "CaseFile", request.getCaseUuid(),
                "Judge " + request.getJudgeUserUuid() + " assigned to case by admin " + adminUuid);
    }

    @Override
    @Transactional
    public void transferJudge(String caseUuid, String newJudgeUserUuid, String reason, String adminUuid) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getStatus() == CaseStatus.DISPOSED || caseFile.getStatus() == CaseStatus.CLOSED) {
            throw new BusinessRuleViolationException("Cannot transfer judge on disposed or closed case.");
        }
        Judge newJudge = judgeRepository.findByUserUuidAndIsDeletedFalse(newJudgeUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Judge", "userUuid", newJudgeUserUuid));
        String oldJudgeUuid = caseFile.getAssignedJudge() != null
                ? caseFile.getAssignedJudge().getUuid() : "NONE";

        caseFile.setAssignedJudge(newJudge);
        caseFileRepository.save(caseFile);

        auditService.logSuccess("JUDGE_TRANSFERRED", "CaseFile", caseUuid,
                "Judge transferred from " + oldJudgeUuid + " to " + newJudgeUserUuid
                        + " by admin " + adminUuid + ". Reason: " + reason);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private JudgeWorkloadResponse toWorkload(Judge judge) {
        Page<CaseFile> assigned = caseFileRepository.findByAssignedJudgeUuidAndIsDeletedFalse(
                judge.getUuid(), org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE));
        long total    = assigned.getTotalElements();
        long disposed = assigned.stream().filter(c -> c.getStatus() == CaseStatus.DISPOSED).count();
        long active   = total - disposed;

        return JudgeWorkloadResponse.builder()
                .judgeUuid(judge.getUuid())
                .judgeIdNumber(judge.getJudgeIdNumber())
                .judgeName(judge.getUser() != null ? judge.getUser().getDisplayName() : "N/A")
                .designation(judge.getDesignation())
                .courtName(judge.getCourtName())
                .specialization(judge.getSpecialization())
                .totalAssignedCases(total)
                .activeCases(active)
                .disposedCases(disposed)
                .pendingHearings(0L)
                .reservedJudgments(assigned.stream()
                        .filter(c -> c.getStatus() == CaseStatus.JUDGEMENT_RESERVED).count())
                .build();
    }
}
