package com.courtai.reports.service.impl;

import com.courtai.audit.repository.AuditLogRepository;
import com.courtai.audit.repository.SecurityAuditRepository;
import com.courtai.reports.dto.response.AuditAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.AuditAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of AuditAnalyticsService.
 */
@Slf4j
@Service("reportsAuditAnalyticsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditAnalyticsServiceImpl implements AuditAnalyticsService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityAuditRepository securityAuditRepository;

    @Override
    public AuditAnalyticsResponse getAuditReport() {
        return AuditAnalyticsResponse.builder()
                .totalAuditLogs(auditLogRepository.count())
                .totalSecurityEvents(securityAuditRepository.count())
                .build();
    }

    @Override
    public List<GraphDataPoint> getActionDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getRoleDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getModuleDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getOutcomeDistribution() {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getAuditGrowthTrend(int year) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getSecurityEventTrend(int year) {
        return List.of();
    }
}
