package com.courtai.judge.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Dashboard aggregate response for the judge portal home screen.
 * Returns counts and short case lists to populate dashboard widgets.
 */
@Getter
@Builder
public class JudgeDashboardResponse {

    /** Total cases currently assigned to this judge. */
    private long assignedCasesCount;

    /** Number of hearings scheduled for today. */
    private long todayHearingsCount;

    /** Cases awaiting judgment (status = JUDGEMENT_RESERVED). */
    private long pendingJudgmentsCount;

    /** Cases with judgment already reserved. */
    private long reservedJudgmentsCount;

    /** Cases fully disposed of. */
    private long disposedCasesCount;

    /** Cases marked as URGENT or HIGH priority. */
    private long urgentCasesCount;

    /** Cases with AI priority score >= 75. */
    private long highAiPriorityCasesCount;

    /** Number of unread notifications for the judge. */
    private long unreadNotificationsCount;

    /** Today's hearings (compact list). */
    private List<JudgeHearingResponse> todayHearings;

    /** Urgent / high-priority cases (compact list). */
    private List<JudgeCaseSummaryResponse> urgentCases;
}
