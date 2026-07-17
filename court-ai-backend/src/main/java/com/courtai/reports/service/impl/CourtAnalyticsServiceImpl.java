package com.courtai.reports.service.impl;

import com.courtai.common.enums.CaseStatus;
import com.courtai.court.entity.Court;
import com.courtai.court.repository.CourtRepository;
import com.courtai.reports.dto.response.CourtReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.AnalyticsMapper;
import com.courtai.reports.mapper.ReportMapper;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.CourtAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CourtAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtAnalyticsServiceImpl implements CourtAnalyticsService {

    private final CourtRepository courtRepository;
    private final ReportQueryRepository reportQueryRepository;
    private final ReportMapper reportMapper;
    private final AnalyticsMapper analyticsMapper;

    @Override
    public CourtReportResponse getCourtReport(Long courtId) {
        log.debug("Generating report for court: {}", courtId);
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found"));
        
        CourtReportResponse response = reportMapper.toCourtReport(court);
        
        response.setTotalCases(reportQueryRepository.countByCourt(courtId));
        response.setPendingCases(reportQueryRepository.countByCourtAndStatus(courtId, CaseStatus.REGISTERED)); // Approximation for pending
        response.setDisposedCases(reportQueryRepository.countByCourtAndStatus(courtId, CaseStatus.DISPOSED));
        response.setFiledToday(reportQueryRepository.countByCourtIdAndFilingDate(courtId, LocalDate.now()));
        
        Double avgPriority = reportQueryRepository.avgPriorityScoreByCourtId(courtId);
        response.setAvgPriorityScore(avgPriority != null ? Math.round(avgPriority * 100.0) / 100.0 : 0.0);
        
        // Populate additional computed fields (mocked or from queries)
        response.setTotalBenches(0); // Add real queries if available
        response.setTotalJudges(0);
        response.setTotalClerks(0);
        
        return response;
    }

    @Override
    public Page<CourtReportResponse> getAllCourtReports(Pageable pageable) {
        return courtRepository.findAll(pageable)
                .map(this::mapCourtWithStats);
    }

    private CourtReportResponse mapCourtWithStats(Court court) {
        CourtReportResponse response = reportMapper.toCourtReport(court);
        response.setTotalCases(reportQueryRepository.countByCourt(court.getId()));
        return response;
    }

    @Override
    public List<GraphDataPoint> getCasesByType(Long courtId) {
        // Mocked or add specific method to ReportQueryRepository taking courtId
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getCasesByStatus(Long courtId) {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getCasesByPriority(Long courtId) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyFilingTrend(Long courtId, int year) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyDisposalTrend(int year) {
        return analyticsMapper.fillMissingMonths(
            analyticsMapper.toMonthlyTimeSeries(reportQueryRepository.monthlyDisposalTrend(year)),
            year
        );
    }

    @Override
    public List<GraphDataPoint> getGlobalStatusDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByStatus());
    }

    @Override
    public List<GraphDataPoint> getGlobalTypeDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByCaseType());
    }
}
