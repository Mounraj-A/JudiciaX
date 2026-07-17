package com.courtai.reports.service;

import com.courtai.reports.dto.request.ReportFilterRequest;
import com.courtai.reports.dto.response.CourtReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Court-level analytics service.
 */
public interface CourtAnalyticsService {

    CourtReportResponse getCourtReport(Long courtId);

    Page<CourtReportResponse> getAllCourtReports(Pageable pageable);

    List<GraphDataPoint> getCasesByType(Long courtId);

    List<GraphDataPoint> getCasesByStatus(Long courtId);

    List<GraphDataPoint> getCasesByPriority(Long courtId);

    List<TimeSeriesDataPoint> getMonthlyFilingTrend(Long courtId, int year);

    List<TimeSeriesDataPoint> getMonthlyDisposalTrend(int year);

    /** Overall status distribution across all courts. */
    List<GraphDataPoint> getGlobalStatusDistribution();

    /** Overall type distribution across all courts. */
    List<GraphDataPoint> getGlobalTypeDistribution();
}
