package com.courtai.clerk.service;

import com.courtai.clerk.dto.ClerkDashboardResponse;

/** Service contract for the clerk dashboard. */
public interface ClerkDashboardService {

    /** Returns aggregated statistics for the clerk's portal dashboard. */
    ClerkDashboardResponse getDashboard();
}
