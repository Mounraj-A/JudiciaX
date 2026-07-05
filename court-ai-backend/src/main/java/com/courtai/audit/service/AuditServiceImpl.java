package com.courtai.audit.service;

import com.courtai.audit.entity.AuditLog;
import com.courtai.audit.repository.AuditLogRepository;
import com.courtai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuditService}.
 *
 * <p>Records audit events asynchronously to avoid impacting request latency.
 * Uses a new transaction (REQUIRES_NEW) so audit logs are persisted even
 * if the calling transaction rolls back.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final org.slf4j.Logger AUDIT_LOGGER =
            org.slf4j.LoggerFactory.getLogger("AUDIT_LOGGER");

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String action, String entityType, String entityUuid, String description) {
        persistAuditLog(action, entityType, entityUuid, description, "SUCCESS");
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String action, String entityType, String entityUuid, String description) {
        persistAuditLog(action, entityType, entityUuid, description, "FAILURE");
    }

    private void persistAuditLog(
            String action, String entityType, String entityUuid,
            String description, String outcome) {

        try {
            AuditLog auditLog = buildAuditLog(action, entityType, entityUuid, description, outcome);
            auditLogRepository.save(auditLog);

            AUDIT_LOGGER.info("[AUDIT] action=[{}] entity=[{}:{}] outcome=[{}] actor=[{}] requestId=[{}]",
                    action, entityType, entityUuid, outcome,
                    auditLog.getActorEmail(), auditLog.getRequestId());

        } catch (Exception ex) {
            // Audit failures must NEVER propagate to the main flow
            log.error("Failed to persist audit log for action [{}]: {}", action, ex.getMessage());
        }
    }

    private AuditLog buildAuditLog(
            String action, String entityType, String entityUuid,
            String description, String outcome) {

        String actorUuid  = null;
        String actorEmail = "SYSTEM";
        String actorRole  = "SYSTEM";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserPrincipal principal) {
            actorUuid  = principal.getUserUuid();
            actorEmail = principal.getUsername();
            actorRole  = principal.getRoleName();
        }

        return AuditLog.builder()
                .actorUuid(actorUuid)
                .actorEmail(actorEmail)
                .actorRole(actorRole)
                .action(action)
                .entityType(entityType)
                .entityUuid(entityUuid)
                .description(description)
                .requestId(MDC.get("requestId"))
                .outcome(outcome)
                .build();
    }
}
