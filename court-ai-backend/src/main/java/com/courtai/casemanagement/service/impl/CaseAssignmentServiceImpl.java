package com.courtai.casemanagement.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseAssignment;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseAssignmentRepository;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casemanagement.dto.CaseAssignmentRequest;
import com.courtai.casemanagement.dto.CaseTransferRequest;
import com.courtai.casemanagement.entity.CaseTransfer;
import com.courtai.casemanagement.repository.CaseTransferRepository;
import com.courtai.casemanagement.service.CaseAssignmentService;
import com.courtai.casemanagement.service.CaseTimelineService;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.court.entity.Court;
import com.courtai.court.entity.CourtBench;
import com.courtai.court.repository.CourtBenchRepository;
import com.courtai.court.repository.CourtRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.repository.JudgeRepository;
import com.courtai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CaseAssignmentServiceImpl implements CaseAssignmentService {

    private final CaseFileRepository       caseFileRepository;
    private final CaseAssignmentRepository assignmentRepository;
    private final CaseTransferRepository   transferRepository;
    private final JudgeRepository          judgeRepository;
    private final ClerkRepository          clerkRepository;
    private final CourtRepository          courtRepository;
    private final CourtBenchRepository     courtBenchRepository;
    private final CaseWorkflowService      workflowService;
    private final CaseTimelineService      timelineService;
    private final AuditService             auditService;
    private final NotificationService      notificationService;

    // ── Judge Assignment ──────────────────────────────────────────────────────

    @Override
    public void assignJudge(String caseUuid, CaseAssignmentRequest req,
                            String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        requireStatus(caseFile, "JUDGE_ASSIGN",
                CaseStatus.REGISTERED, CaseStatus.UNDER_REVIEW);

        Judge judge = judgeRepository.findByUserUuidAndIsDeletedFalse(req.getAssigneeUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Judge", "userUuid", req.getAssigneeUuid()));

        // Deactivate existing active assignment
        assignmentRepository.findByCaseFileIdAndIsActiveTrueAndIsDeletedFalse(caseFile.getId())
                .ifPresent(prev -> {
                    prev.setIsActive(Boolean.FALSE);
                    prev.setUnassignedAt(LocalDateTime.now());
                    assignmentRepository.save(prev);
                });

        // Create new assignment
        assignmentRepository.save(CaseAssignment.builder()
                .caseFile(caseFile)
                .judge(judge)
                .assignedAt(LocalDateTime.now())
                .assignmentReason(req.getReason())
                .assignedByUuid(actorUuid)
                .isActive(Boolean.TRUE)
                .build());

        // Update case
        caseFile.setAssignedJudge(judge);
        caseFileRepository.save(caseFile);

        // Workflow transition
        workflowService.executeTransition(caseUuid, CaseStatus.JUDGE_ASSIGNED,
                actorUuid, actorRole,
                "Judge assigned: " + judge.getJudgeIdNumber());

        // Timeline
        timelineService.recordEvent(caseFile, "JUDGE_ASSIGNED", "Judge Assigned",
                actorUuid, actorRole, null, req.getReason());

        // Notify judge
        if (judge.getUser() != null) {
            notificationService.sendInAppNotification(judge.getUser().getUuid(),
                    "Case Assigned",
                    "Case " + caseFile.getCaseNumber() + " has been assigned to you.",
                    caseFile.getUuid(), "CaseFile");
        }

        auditService.logSuccess("JUDGE_ASSIGNED", "CaseFile", caseUuid,
                "Judge " + judge.getUuid() + " assigned by " + actorUuid);
    }

    // ── Judge Transfer ────────────────────────────────────────────────────────

    @Override
    public void transferJudge(String caseUuid, CaseTransferRequest req,
                              String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        Judge newJudge = judgeRepository.findByUserUuidAndIsDeletedFalse(req.getTargetUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Judge", "userUuid", req.getTargetUuid()));

        String oldJudgeName = caseFile.getAssignedJudge() != null
                ? caseFile.getAssignedJudge().getJudgeIdNumber() : "UNASSIGNED";

        // Deactivate existing assignment
        assignmentRepository.findByCaseFileIdAndIsActiveTrueAndIsDeletedFalse(caseFile.getId())
                .ifPresent(prev -> {
                    prev.setIsActive(Boolean.FALSE);
                    prev.setUnassignedAt(LocalDateTime.now());
                    assignmentRepository.save(prev);
                });

        // New assignment
        assignmentRepository.save(CaseAssignment.builder()
                .caseFile(caseFile)
                .judge(newJudge)
                .assignedAt(LocalDateTime.now())
                .assignmentReason(req.getReason())
                .assignedByUuid(actorUuid)
                .isActive(Boolean.TRUE)
                .build());

        // Transfer record
        recordTransfer(caseFile, "JUDGE",
                caseFile.getAssignedJudge() != null ? caseFile.getAssignedJudge().getUuid() : null,
                oldJudgeName,
                newJudge.getUuid(), newJudge.getJudgeIdNumber(),
                req.getReason(), actorUuid, actorRole);

        caseFile.setAssignedJudge(newJudge);
        caseFileRepository.save(caseFile);

        timelineService.recordEvent(caseFile, "JUDGE_TRANSFERRED", "Judge Transferred",
                actorUuid, actorRole, null, req.getReason());
        auditService.logSuccess("JUDGE_TRANSFERRED", "CaseFile", caseUuid,
                "Judge transferred to " + newJudge.getUuid() + " by " + actorUuid);
    }

    // ── Clerk Assignment ──────────────────────────────────────────────────────

    @Override
    public void assignClerk(String caseUuid, CaseAssignmentRequest req,
                            String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        clerkRepository.findByUserUuidAndIsDeletedFalse(req.getAssigneeUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Clerk", "userUuid", req.getAssigneeUuid()));

        caseFile.setScrutinyClerkUuid(req.getAssigneeUuid());
        caseFileRepository.save(caseFile);

        timelineService.recordEvent(caseFile, "CLERK_ASSIGNED", "Clerk Assigned",
                actorUuid, actorRole, null, req.getReason());
        auditService.logSuccess("CLERK_ASSIGNED", "CaseFile", caseUuid,
                "Clerk " + req.getAssigneeUuid() + " assigned by " + actorUuid);
    }

    // ── Clerk Transfer ────────────────────────────────────────────────────────

    @Override
    public void transferClerk(String caseUuid, CaseTransferRequest req,
                              String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        String oldClerk = caseFile.getScrutinyClerkUuid();

        clerkRepository.findByUserUuidAndIsDeletedFalse(req.getTargetUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Clerk", "userUuid", req.getTargetUuid()));

        recordTransfer(caseFile, "CLERK",
                oldClerk, "CLERK:" + oldClerk,
                req.getTargetUuid(), "CLERK:" + req.getTargetUuid(),
                req.getReason(), actorUuid, actorRole);

        caseFile.setScrutinyClerkUuid(req.getTargetUuid());
        caseFileRepository.save(caseFile);

        timelineService.recordEvent(caseFile, "CLERK_TRANSFERRED", "Clerk Transferred",
                actorUuid, actorRole, null, req.getReason());
        auditService.logSuccess("CLERK_TRANSFERRED", "CaseFile", caseUuid,
                "Clerk transferred to " + req.getTargetUuid() + " by " + actorUuid);
    }

    // ── Court Transfer ────────────────────────────────────────────────────────

    @Override
    public void transferCourt(String caseUuid, CaseTransferRequest req,
                              String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        if (caseFile.getStatus() == CaseStatus.DISPOSED
                || caseFile.getStatus() == CaseStatus.CLOSED
                || caseFile.getStatus() == CaseStatus.ARCHIVED) {
            throw new BusinessRuleViolationException(
                    "Cannot transfer a disposed, closed, or archived case.");
        }

        Court newCourt = courtRepository.findByUuidAndIsDeletedFalse(req.getTargetUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Court", "uuid", req.getTargetUuid()));

        String oldCourtName = caseFile.getCourt() != null
                ? caseFile.getCourt().getCourtName() : "UNASSIGNED";
        String oldCourtUuid = caseFile.getCourt() != null
                ? caseFile.getCourt().getUuid() : null;

        recordTransfer(caseFile, "COURT",
                oldCourtUuid, oldCourtName,
                newCourt.getUuid(), newCourt.getCourtName(),
                req.getReason(), actorUuid, actorRole);

        caseFile.setCourt(newCourt);
        caseFile.setCourtName(newCourt.getCourtName());
        caseFile.setStatus(CaseStatus.TRANSFERRED);
        caseFileRepository.save(caseFile);

        timelineService.recordEvent(caseFile, "COURT_TRANSFERRED",
                "Case Transferred to " + newCourt.getCourtName(),
                actorUuid, actorRole, null, req.getReason());

        auditService.logSuccess("COURT_TRANSFERRED", "CaseFile", caseUuid,
                "Court transferred to " + newCourt.getUuid() + " by " + actorUuid);
    }

    // ── Bench Assignment ──────────────────────────────────────────────────────

    @Override
    public void assignBench(String caseUuid, CaseAssignmentRequest req,
                            String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        CourtBench bench = courtBenchRepository.findByUuidAndIsDeletedFalse(req.getAssigneeUuid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CourtBench", "uuid", req.getAssigneeUuid()));

        // Validate bench belongs to the case's court
        if (caseFile.getCourt() != null
                && !bench.getCourt().getId().equals(caseFile.getCourt().getId())) {
            throw new BusinessRuleViolationException(
                    "Bench does not belong to the case's assigned court.");
        }

        timelineService.recordEvent(caseFile, "BENCH_ASSIGNED",
                "Bench Assigned: " + bench.getBenchNumber(),
                actorUuid, actorRole, null, req.getReason());
        auditService.logSuccess("BENCH_ASSIGNED", "CaseFile", caseUuid,
                "Bench " + bench.getUuid() + " assigned by " + actorUuid);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CaseFile loadCase(String caseUuid) {
        return caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
    }

    private void requireStatus(CaseFile c, String action, CaseStatus... allowed) {
        for (CaseStatus s : allowed) {
            if (c.getStatus() == s) return;
        }
        throw new BusinessRuleViolationException(
                "Action " + action + " requires status one of: "
                        + java.util.Arrays.toString(allowed)
                        + ". Current: " + c.getStatus());
    }

    private void recordTransfer(CaseFile caseFile, String type,
                                String fromUuid, String fromName,
                                String toUuid, String toName,
                                String reason, String actorUuid, String actorRole) {
        transferRepository.save(CaseTransfer.builder()
                .caseFile(caseFile)
                .transferType(type)
                .fromEntityUuid(fromUuid)
                .fromEntityName(fromName)
                .toEntityUuid(toUuid)
                .toEntityName(toName)
                .reason(reason)
                .transferredByUuid(actorUuid)
                .transferredByRole(actorRole)
                .transferredAt(LocalDateTime.now())
                .build());
    }
}
