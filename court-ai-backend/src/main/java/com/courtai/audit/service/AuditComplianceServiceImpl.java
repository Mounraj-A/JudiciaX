package com.courtai.audit.service;

import com.courtai.audit.dto.ComplianceAuditResponse;
import com.courtai.audit.entity.ComplianceAudit;
import com.courtai.audit.mapper.ComplianceAuditMapper;
import com.courtai.audit.repository.ComplianceAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditComplianceServiceImpl implements AuditComplianceService {

    private final ComplianceAuditRepository complianceAuditRepository;
    private final ComplianceAuditMapper complianceAuditMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logComplianceViolation(String correlationId, String violationType, String details, String status) {
        ComplianceAudit audit = ComplianceAudit.builder()
                .correlationId(correlationId)
                .violationType(violationType)
                .details(details)
                .complianceStatus(status)
                .build();
        complianceAuditRepository.save(audit);
        log.warn("[COMPLIANCE AUDIT] Logged violation {} for correlation {}", violationType, correlationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceAuditResponse> getViolations() {
        return complianceAuditMapper.toResponseList(complianceAuditRepository.findAll());
    }
}
