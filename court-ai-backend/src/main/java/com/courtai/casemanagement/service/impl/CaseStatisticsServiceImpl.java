package com.courtai.casemanagement.service.impl;

import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casemanagement.dto.CaseStatisticsResponse;
import com.courtai.casemanagement.service.CaseStatisticsService;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseStatisticsServiceImpl implements CaseStatisticsService {

    private final CaseFileRepository caseFileRepository;

    @Override
    public CaseStatisticsResponse getGlobalStatistics() {
        return buildStatistics(null, null, null, "GLOBAL");
    }

    @Override
    public CaseStatisticsResponse getStatisticsByCourt(String courtUuid) {
        return buildStatistics(courtUuid, null, null, "COURT:" + courtUuid);
    }

    @Override
    public CaseStatisticsResponse getStatisticsByJudge(String judgeUserUuid) {
        return buildStatistics(null, judgeUserUuid, null, "JUDGE:" + judgeUserUuid);
    }

    @Override
    public CaseStatisticsResponse getStatisticsByAdvocate(String advocateUuid) {
        return buildStatistics(null, null, advocateUuid, "ADVOCATE:" + advocateUuid);
    }

    // ── Internal aggregation ──────────────────────────────────────────────────

    private CaseStatisticsResponse buildStatistics(String courtUuid, String judgeUserUuid,
                                                    String advocateUuid, String label) {
        // Use findAll with specs or count queries.
        // Simple approach: retrieve all matching cases and aggregate in-memory
        // (acceptable for government deployments with bounded dataset sizes).
        // Phase 2 will replace with native SQL aggregation queries.

        var allCases = caseFileRepository.findAll(
                (root, query, cb) -> {
                    var predicates = new java.util.ArrayList<>();
                    predicates.add(cb.isFalse(root.get("isDeleted")));
                    if (courtUuid != null)
                        predicates.add(cb.equal(root.get("court").get("uuid"), courtUuid));
                    if (judgeUserUuid != null)
                        predicates.add(cb.equal(root.get("assignedJudge").get("user").get("uuid"), judgeUserUuid));
                    if (advocateUuid != null)
                        predicates.add(cb.or(
                                cb.equal(root.get("petitionerAdvocate").get("uuid"), advocateUuid),
                                cb.equal(root.get("respondentAdvocate").get("uuid"), advocateUuid)));
                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                });

        long total       = allCases.size();
        long disposed    = count(allCases, CaseStatus.DISPOSED);
        long closed      = count(allCases, CaseStatus.CLOSED);
        long archived    = count(allCases, CaseStatus.ARCHIVED);
        long cancelled   = count(allCases, CaseStatus.CANCELLED);
        long transferred = count(allCases, CaseStatus.TRANSFERRED);
        long aiPending   = allCases.stream().filter(c -> c.getPriorityScore() == null).count();
        long high        = allCases.stream().filter(c ->
                c.getPriority() == CasePriority.HIGH || c.getPriority() == CasePriority.CRITICAL).count();
        long critical    = count(allCases, CasePriority.CRITICAL);

        long pending = total - disposed - closed - archived - cancelled;

        return CaseStatisticsResponse.builder()
                .totalCases(total)
                .pendingCases(pending)
                .disposedCases(disposed)
                .closedCases(closed)
                .archivedCases(archived)
                .cancelledCases(cancelled)
                .transferredCases(transferred)
                .aiPendingCases(aiPending)
                .highPriorityCases(high)
                .criticalCases(critical)
                // Per-stage counts
                .draftCount(count(allCases, CaseStatus.DRAFT))
                .submittedCount(count(allCases, CaseStatus.SUBMITTED))
                .underScrutinyCount(count(allCases, CaseStatus.UNDER_SCRUTINY))
                .registeredCount(count(allCases, CaseStatus.REGISTERED))
                .judgeAssignedCount(count(allCases, CaseStatus.JUDGE_ASSIGNED))
                .hearingScheduledCount(count(allCases, CaseStatus.HEARING_SCHEDULED))
                .adjournedCount(count(allCases, CaseStatus.ADJOURNED))
                .judgmentReservedCount(count(allCases, CaseStatus.JUDGEMENT_RESERVED))
                // Performance metrics (Phase 2: compute from hearing/disposal dates)
                .avgDisposalDays(null)
                .avgHearingsPerCase(null)
                .avgHearingDelayDays(null)
                // Scope metadata
                .reportedForCourtUuid(courtUuid)
                .reportedForJudgeUuid(judgeUserUuid)
                .reportedForAdvocateUuid(advocateUuid)
                .periodLabel(label)
                .build();
    }

    private long count(java.util.List<com.courtai.casefile.entity.CaseFile> cases, CaseStatus status) {
        return cases.stream().filter(c -> c.getStatus() == status).count();
    }

    private long count(java.util.List<com.courtai.casefile.entity.CaseFile> cases, CasePriority priority) {
        return cases.stream().filter(c -> c.getPriority() == priority).count();
    }
}
