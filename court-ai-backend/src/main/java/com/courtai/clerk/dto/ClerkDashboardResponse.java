package com.courtai.clerk.dto;

import lombok.Builder;
import lombok.Data;

/** Aggregated statistics for the clerk dashboard. */
@Data
@Builder
public class ClerkDashboardResponse {
    private String clerkName;
    private String courtName;
    private String employeeId;

    // Case counts
    private long pendingScrutinyCount;
    private long underScrutinyCount;
    private long returnedCasesCount;
    private long registeredTodayCount;

    // Verification counts
    private long pendingDocumentVerificationCount;
    private long pendingEvidenceVerificationCount;

    // Other
    private long duplicateAlertCount;
    private long pendingJudgeAssignmentCount;
    private long unreadNotificationsCount;

    private String welcomeMessage;
}
