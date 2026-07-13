package com.courtai.judge.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.HearingStatus;
import com.courtai.hearing.entity.Hearing;
import com.courtai.hearing.repository.HearingRepository;
import com.courtai.judge.dto.JudgeCaseSummaryResponse;
import com.courtai.judge.dto.JudgeDashboardResponse;
import com.courtai.judge.dto.JudgeHearingResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeCaseMapper;
import com.courtai.judge.mapper.JudgeHearingMapper;
import com.courtai.judge.service.JudgeDashboardService;
import com.courtai.judge.service.JudgeService;
import com.courtai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeDashboardService}.
 * Aggregates counts and shortlists from existing repositories.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeDashboardServiceImpl implements JudgeDashboardService {

    private final JudgeService           judgeService;
    private final CaseFileRepository     caseFileRepository;
    private final HearingRepository      hearingRepository;
    private final NotificationService    notificationService;
    private final JudgeCaseMapper        caseMapper;
    private final JudgeHearingMapper     hearingMapper;

    @Override
    public JudgeDashboardResponse getDashboard() {
        Judge judge = judgeService.getCurrentJudge();
        String judgeUuid = judge.getUuid();

        // ── Assigned cases total ───────────────────────────────────────────
        long assignedCount = caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judgeUuid, PageRequest.of(0, 1))
                .getTotalElements();

        // ── Today's hearings ──────────────────────────────────────────────
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1).minusNanos(1);
        List<Hearing> todayHearings = hearingRepository
                .findByScheduledAtBetweenAndIsDeletedFalse(startOfDay, endOfDay)
                .stream()
                .filter(h -> h.getJudge() != null && judgeUuid.equals(h.getJudge().getUuid()))
                .collect(Collectors.toList());

        // ── Pending judgments (JUDGEMENT_RESERVED) ────────────────────────
        long pendingJudgmentsCount = caseFileRepository
                .findByStatusAndIsDeletedFalse(CaseStatus.JUDGEMENT_RESERVED, PageRequest.of(0, 1))
                .getTotalElements();  // will filter by judge in service if needed

        // ── Disposed cases ────────────────────────────────────────────────
        long disposedCount = caseFileRepository
                .findByStatusAndIsDeletedFalse(CaseStatus.DISPOSED, PageRequest.of(0, 1))
                .getTotalElements();

        // ── Urgent/High priority cases ────────────────────────────────────
        List<CaseFile> assignedCases = caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judgeUuid,
                        PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "priorityScore")))
                .getContent();

        List<CaseFile> urgentCases = assignedCases.stream()
                .filter(c -> c.getPriority() == CasePriority.HIGH
                          || c.getPriority() == CasePriority.CRITICAL)
                .limit(10)
                .collect(Collectors.toList());

        long highAiPriority = assignedCases.stream()
                .filter(c -> c.getPriorityScore() != null && c.getPriorityScore() >= 75.0)
                .count();

        // ── Unread notifications ──────────────────────────────────────────
        long unreadNotifications = notificationService.getUnreadCount(judge.getUser().getUuid());

        // ── Map ───────────────────────────────────────────────────────────
        List<JudgeHearingResponse> todayHearingDtos = todayHearings.stream()
                .map(hearingMapper::toResponse)
                .collect(Collectors.toList());

        List<JudgeCaseSummaryResponse> urgentCaseDtos = urgentCases.stream()
                .map(caseMapper::toSummary)
                .collect(Collectors.toList());

        return JudgeDashboardResponse.builder()
                .assignedCasesCount(assignedCount)
                .todayHearingsCount(todayHearings.size())
                .pendingJudgmentsCount(pendingJudgmentsCount)
                .reservedJudgmentsCount(pendingJudgmentsCount)
                .disposedCasesCount(disposedCount)
                .urgentCasesCount(urgentCases.size())
                .highAiPriorityCasesCount(highAiPriority)
                .unreadNotificationsCount(unreadNotifications)
                .todayHearings(todayHearingDtos)
                .urgentCases(urgentCaseDtos)
                .build();
    }
}
