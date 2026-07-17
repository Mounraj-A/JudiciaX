package com.courtai.reports.service;

import com.courtai.reports.dto.request.ReportFilterRequest;
import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Case-level analytics service.
 */
public interface CaseAnalyticsService {

    CaseReportResponse getCaseReport(String caseUuid);

    Page<CaseReportResponse> getCasesByStatus(String status, Pageable pageable);

    Page<CaseReportResponse> getCasesByType(String type, Pageable pageable);

    List<GraphDataPoint> getStatusDistribution();

    List<GraphDataPoint> getTypeDistribution();

    List<GraphDataPoint> getPriorityDistribution();

    List<GraphDataPoint> getAgeDistribution();

    List<TimeSeriesDataPoint> getMonthlyFilingTrend(int year);

    List<TimeSeriesDataPoint> getMonthlyDisposalTrend(int year);
}
