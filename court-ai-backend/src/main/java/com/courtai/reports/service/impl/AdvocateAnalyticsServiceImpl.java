package com.courtai.reports.service.impl;

import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.repository.AdvocateRepository;
import com.courtai.reports.dto.response.AdvocateReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.ReportMapper;
import com.courtai.reports.service.AdvocateAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of AdvocateAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvocateAnalyticsServiceImpl implements AdvocateAnalyticsService {

    private final AdvocateRepository advocateRepository;
    private final ReportMapper reportMapper;

    @Override
    public AdvocateReportResponse getAdvocateReport(String advocateUuid) {
        Advocate advocate = advocateRepository.findByUuidAndIsDeletedFalse(advocateUuid)
                .orElseThrow(() -> new RuntimeException("Advocate not found"));
        return buildAdvocateReport(advocate);
    }

    @Override
    public AdvocateReportResponse getAdvocateReportByPrincipal(String principalUsername) {
        Advocate advocate = advocateRepository.findByUserUsernameAndIsDeletedFalse(principalUsername)
                .orElseThrow(() -> new RuntimeException("Advocate profile not found for current user"));
        return buildAdvocateReport(advocate);
    }

    @Override
    public Page<AdvocateReportResponse> getAllAdvocateReports(Pageable pageable) {
        return advocateRepository.findAll(pageable).map(this::buildAdvocateReport);
    }

    @Override
    public List<GraphDataPoint> getCasesByStatus(String advocateUuid) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyFilingTrend(String advocateUuid, int year) {
        return List.of();
    }

    private AdvocateReportResponse buildAdvocateReport(Advocate advocate) {
        AdvocateReportResponse response = reportMapper.toAdvocateReport(advocate);
        response.setFiledCases(0); // Mocked fields for now, fetch via repos
        return response;
    }
}
