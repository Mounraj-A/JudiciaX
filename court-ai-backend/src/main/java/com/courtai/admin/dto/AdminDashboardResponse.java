package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Full admin dashboard response containing all statistics and lists.
 */
@Getter
@Builder
public class AdminDashboardResponse {

    // ── User Statistics ───────────────────────────────────────────────────
    private long totalUsers;
    private long pendingApprovals;
    private long lockedUsers;
    private long activeUsers;
    private long totalAdvocates;
    private long totalJudges;
    private long totalClerks;
    private long totalAdmins;

    // ── Court Statistics ──────────────────────────────────────────────────
    private long totalCourts;
    private long totalBenches;
    private long totalCourtRooms;

    // ── Case Statistics ───────────────────────────────────────────────────
    private long registeredCases;
    private long pendingCases;
    private long disposedCases;
    private long todayHearings;

    // ── System Statistics ─────────────────────────────────────────────────
    private long unreadNotifications;
    private long auditEventsToday;
    private long activeMaintenanceWindows;
    private long activeAnnouncements;

    // ── Security Statistics ───────────────────────────────────────────────
    private long failedLoginsToday;
    private long activeSessions;
    private long highSeveritySecurityEvents;

    // ── AI Statistics ─────────────────────────────────────────────────────
    private boolean aiEnabled;
    private String aiModelVersion;
    private long aiAnalyzedCasesToday;

    // ── Shortlists ────────────────────────────────────────────────────────
    private List<UserSummaryResponse> recentPendingUsers;
}
