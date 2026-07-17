package com.courtai.reports.service;

import com.courtai.reports.dto.response.ClerkReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Clerk performance analytics service.
 */
public interface ClerkAnalyticsService {

    ClerkReportResponse getClerkReport(String clerkUuid);

    /** Resolves clerk UUID from JWT principal (email). */
    ClerkReportResponse getClerkReportByPrincipal(String principalUsername);

    Page<ClerkReportResponse> getAllClerkReports(Pageable pageable);

    List<GraphDataPoint> getDuplicateDetectionByCourt();

    List<TimeSeriesDataPoint> getMonthlyRegistrationTrend(String clerkUuid, int year);
}
