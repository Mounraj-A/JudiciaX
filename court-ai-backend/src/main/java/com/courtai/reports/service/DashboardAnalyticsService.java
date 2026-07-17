package com.courtai.reports.service;

import com.courtai.reports.dto.response.DashboardSummaryResponse;

/**
 * Dashboard analytics service — one method per role dashboard.
 *
 * <p>All methods are read-only and resolve the calling user's context
 * from the principal (JWT) at the service layer.</p>
 */
public interface DashboardAnalyticsService {

    /** Court-level global dashboard. Requires ADMIN or SUPER_ADMIN. */
    DashboardSummaryResponse getCourtDashboard();

    /**
     * Judge self-dashboard. Resolves the judge profile from the JWT principal username.
     *
     * @param principalUsername the authenticated user's email/username from JWT
     */
    DashboardSummaryResponse getJudgeDashboard(String principalUsername);

    /**
     * Advocate self-dashboard. Resolves the advocate profile from JWT.
     *
     * @param principalUsername the authenticated user's email/username from JWT
     */
    DashboardSummaryResponse getAdvocateDashboard(String principalUsername);

    /**
     * Clerk self-dashboard. Resolves the clerk profile from JWT.
     *
     * @param principalUsername the authenticated user's email/username from JWT
     */
    DashboardSummaryResponse getClerkDashboard(String principalUsername);

    /** Admin system dashboard. Requires ADMIN or SUPER_ADMIN. */
    DashboardSummaryResponse getAdminDashboard();
}
