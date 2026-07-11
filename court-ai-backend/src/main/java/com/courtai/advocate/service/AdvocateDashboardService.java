package com.courtai.advocate.service;

import com.courtai.advocate.dto.AdvocateDashboardResponse;

/**
 * Service contract for the advocate dashboard.
 */
public interface AdvocateDashboardService {

    /**
     * Returns aggregated statistics for the advocate's portal dashboard.
     */
    AdvocateDashboardResponse getDashboard();
}
