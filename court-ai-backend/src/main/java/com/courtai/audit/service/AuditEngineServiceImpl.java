package com.courtai.audit.service;

import com.courtai.audit.entity.AuditEvent;
import com.courtai.audit.repository.AuditEventRepository;
import com.courtai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEngineServiceImpl implements AuditEngineService {

    private final AuditEventRepository auditEventRepository;
    private final AuditIntegrityService auditIntegrityService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent publishEvent(String module, String action, String entityType, String entityUuid, String remarks) {
        String correlationId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return publishEventWithCorrelation(correlationId, module, action, entityType, entityUuid, remarks);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent publishEventWithCorrelation(String correlationId, String module, String action, String entityType, String entityUuid, String remarks) {
        String actorUuid = null;
        String actorName = "SYSTEM";
        String actorRole = "SYSTEM";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserPrincipal principal) {
            actorUuid = principal.getUserUuid();
            actorName = principal.getUsername();
            actorRole = principal.getRoleName();
        }

        AuditEvent event = AuditEvent.builder()
                .correlationId(correlationId)
                .module(module)
                .action(action)
                .entityType(entityType)
                .entityUuid(entityUuid)
                .actorUuid(actorUuid)
                .actorName(actorName)
                .actorRole(actorRole)
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .ipAddress("127.0.0.1") // Can be retrieved from request context in real scenario
                .browser("System")
                .device("Server")
                .remarks(remarks)
                .build();

        AuditEvent savedEvent = auditEventRepository.save(event);
        auditIntegrityService.generateAndStoreHash(savedEvent);
        
        log.info("[AUDIT ENGINE] Published event {} for action {} with correlation {}", savedEvent.getUuid(), action, correlationId);
        return savedEvent;
    }
}
