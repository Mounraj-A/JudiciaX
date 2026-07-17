package com.courtai.audit.service;

import com.courtai.audit.entity.SecurityAudit;
import com.courtai.audit.repository.SecurityAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAuditServiceImpl implements SecurityAuditService {

    private final SecurityAuditRepository securityAuditRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSecurityEvent(String correlationId, String eventType, String ipAddress, String details) {
        SecurityAudit audit = SecurityAudit.builder()
                .correlationId(correlationId)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .details(details)
                .build();
        securityAuditRepository.save(audit);
        log.warn("[SECURITY AUDIT] Logged event {} for IP {}", eventType, ipAddress);
    }
}
