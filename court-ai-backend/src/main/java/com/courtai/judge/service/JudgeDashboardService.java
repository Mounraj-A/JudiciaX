package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeDashboardResponse;

/**
 * Service contract for the judge dashboard.
 * Returns aggregate counts and shortlists for the judge's home screen.
 */
public interface JudgeDashboardService {

    /**
     * Builds the complete dashboard response for the currently authenticated judge.
     */
    JudgeDashboardResponse getDashboard();
}
