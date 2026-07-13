package com.courtai.casemanagement.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.casemanagement.service.CaseTimelineService;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enforces the judicial case state machine.
 * All status transitions in the system MUST flow through this service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CaseWorkflowServiceImpl implements CaseWorkflowService {

    // ── Transition Matrix ─────────────────────────────────────────────────────
    private static final Map<CaseStatus, Set<CaseStatus>> TRANSITIONS = Map.ofEntries(
            Map.entry(CaseStatus.DRAFT,
                    EnumSet.of(CaseStatus.SUBMITTED, CaseStatus.CANCELLED)),
            Map.entry(CaseStatus.SUBMITTED,
                    EnumSet.of(CaseStatus.UNDER_SCRUTINY, CaseStatus.RETURNED)),
            Map.entry(CaseStatus.UNDER_SCRUTINY,
                    EnumSet.of(CaseStatus.REGISTERED, CaseStatus.RETURNED, CaseStatus.REJECTED)),
            Map.entry(CaseStatus.RETURNED,
                    EnumSet.of(CaseStatus.SUBMITTED, CaseStatus.CANCELLED)),
            Map.entry(CaseStatus.REGISTERED,
                    EnumSet.of(CaseStatus.UNDER_REVIEW, CaseStatus.JUDGE_ASSIGNED)),
            Map.entry(CaseStatus.UNDER_REVIEW,
                    EnumSet.of(CaseStatus.JUDGE_ASSIGNED)),
            Map.entry(CaseStatus.JUDGE_ASSIGNED,
                    EnumSet.of(CaseStatus.AI_ANALYZED, CaseStatus.HEARING_SCHEDULED)),
            Map.entry(CaseStatus.AI_ANALYZED,
                    EnumSet.of(CaseStatus.HEARING_SCHEDULED)),
            Map.entry(CaseStatus.HEARING_SCHEDULED,
                    EnumSet.of(CaseStatus.IN_PROGRESS, CaseStatus.IN_HEARING, CaseStatus.ADJOURNED)),
            Map.entry(CaseStatus.IN_HEARING,
                    EnumSet.of(CaseStatus.ADJOURNED, CaseStatus.JUDGEMENT_RESERVED, CaseStatus.IN_PROGRESS)),
            Map.entry(CaseStatus.IN_PROGRESS,
                    EnumSet.of(CaseStatus.ADJOURNED, CaseStatus.JUDGEMENT_RESERVED, CaseStatus.HEARING_SCHEDULED)),
            Map.entry(CaseStatus.ADJOURNED,
                    EnumSet.of(CaseStatus.HEARING_SCHEDULED, CaseStatus.IN_PROGRESS)),
            Map.entry(CaseStatus.JUDGEMENT_RESERVED,
                    EnumSet.of(CaseStatus.DISPOSED)),
            Map.entry(CaseStatus.DISPOSED,
                    EnumSet.of(CaseStatus.CLOSED, CaseStatus.ARCHIVED, CaseStatus.APPEALED)),
            Map.entry(CaseStatus.CLOSED,
                    EnumSet.of(CaseStatus.ARCHIVED)),
            // Legacy statuses with sensible forward paths
            Map.entry(CaseStatus.FILED,
                    EnumSet.of(CaseStatus.UNDER_SCRUTINY, CaseStatus.ADMITTED)),
            Map.entry(CaseStatus.ADMITTED,
                    EnumSet.of(CaseStatus.PENDING_HEARING, CaseStatus.JUDGE_ASSIGNED)),
            Map.entry(CaseStatus.PENDING_HEARING,
                    EnumSet.of(CaseStatus.IN_HEARING, CaseStatus.HEARING_SCHEDULED, CaseStatus.ADJOURNED)),
            // Terminal — no transitions
            Map.entry(CaseStatus.ARCHIVED,   EnumSet.noneOf(CaseStatus.class)),
            Map.entry(CaseStatus.CANCELLED,  EnumSet.noneOf(CaseStatus.class)),
            Map.entry(CaseStatus.REJECTED,   EnumSet.noneOf(CaseStatus.class)),
            Map.entry(CaseStatus.APPEALED,   EnumSet.noneOf(CaseStatus.class)),
            Map.entry(CaseStatus.TRANSFERRED, EnumSet.noneOf(CaseStatus.class))
    );

    private final CaseFileRepository          caseFileRepository;
    private final CaseStatusHistoryRepository statusHistoryRepository;
    private final CaseTimelineService         timelineService;
    private final AuditService                auditService;

    @Override
    public List<CaseStatus> getAllowedTransitions(CaseStatus current) {
        Set<CaseStatus> allowed = TRANSITIONS.getOrDefault(current, EnumSet.noneOf(CaseStatus.class));
        return List.copyOf(allowed);
    }

    @Override
    public void validateTransition(CaseStatus from, CaseStatus to) {
        Set<CaseStatus> allowed = TRANSITIONS.getOrDefault(from, EnumSet.noneOf(CaseStatus.class));
        if (!allowed.contains(to)) {
            throw new BusinessRuleViolationException(
                    "Invalid workflow transition: " + from.name() + " → " + to.name()
                            + ". Allowed: " + allowed);
        }
    }

    @Override
    @Transactional
    public String executeTransition(String caseUuid, CaseStatus toStatus,
                                    String actorUuid, String actorRole, String reason) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        CaseStatus fromStatus = caseFile.getStatus();
        validateTransition(fromStatus, toStatus);

        // Persist status change
        caseFile.setStatus(toStatus);
        caseFileRepository.save(caseFile);

        // Record immutable status history
        statusHistoryRepository.save(CaseStatusHistory.builder()
                .caseFile(caseFile)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .changedAt(LocalDateTime.now())
                .changedByUuid(actorUuid)
                .changedByRole(actorRole)
                .remarks(reason)
                .build());

        // Record timeline event
        timelineService.recordEvent(caseFile,
                "STATUS_CHANGED",
                fromStatus.name() + " → " + toStatus.name(),
                actorUuid, actorRole, null,
                reason);

        // Async audit
        auditService.logSuccess("CASE_STATUS_CHANGED", "CaseFile", caseUuid,
                "Status: " + fromStatus.name() + " → " + toStatus.name()
                        + " | Actor: " + actorUuid + " | Reason: " + reason);

        log.info("[WORKFLOW] Case {} transitioned {} → {} by {}", caseUuid, fromStatus, toStatus, actorUuid);
        return caseUuid;
    }
}
