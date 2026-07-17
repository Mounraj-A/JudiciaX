package com.courtai.audit.service;

import com.courtai.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditAnalyticsServiceImpl implements AuditAnalyticsService {

    private final AuditEventRepository auditEventRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> stats = new HashMap<>();
        long totalEvents = auditEventRepository.count();
        stats.put("totalEvents", totalEvents);
        
        // Placeholder for real aggregated stats
        stats.put("mostActiveModule", "CASE_MANAGEMENT");
        stats.put("failedLogins", 5);
        stats.put("permissionViolations", 2);
        
        return stats;
    }
}
