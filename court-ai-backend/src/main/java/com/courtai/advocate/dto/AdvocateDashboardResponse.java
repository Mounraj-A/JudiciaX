package com.courtai.advocate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Dashboard summary for the advocate portal home screen.
 */
@Getter
@Builder
public class AdvocateDashboardResponse {

    private String advocateName;
    private String barCouncilNumber;
    private boolean profileComplete;
    private boolean profileVerified;

    // Case statistics
    private long totalCases;
    private long activeCases;
    private long pendingCases;
    private long closedCases;

    // Today's hearings
    private long hearingsTodayCount;

    // Notifications
    private long unreadNotificationsCount;

    // Profile completion hint
    private String profileCompletionMessage;
}
