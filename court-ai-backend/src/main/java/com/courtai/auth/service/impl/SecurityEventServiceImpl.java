package com.courtai.auth.service.impl;

import com.courtai.auth.entity.SecurityEvent;
import com.courtai.auth.repository.SecurityEventRepository;
import com.courtai.auth.service.SecurityEventService;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Async implementation of {@link SecurityEventService}.
 *
 * <p>Uses {@code REQUIRES_NEW} transaction so event records persist even
 * when the calling transaction rolls back.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityEventServiceImpl implements SecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(SecurityEventType type, User user, String description,
                       String ipAddress, String device, String browser, String severity) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                    .user(user)
                    .eventType(type)
                    .description(description)
                    .ipAddress(ipAddress)
                    .device(device)
                    .browser(browser)
                    .severity(severity)
                    .eventTime(LocalDateTime.now())
                    .actorEmail(user != null ? user.getEmail() : null)
                    .build();
            securityEventRepository.save(event);
            log.info("[SECURITY] type=[{}] actor=[{}] ip=[{}] severity=[{}]",
                    type, user != null ? user.getEmail() : "ANONYMOUS", ipAddress, severity);
        } catch (Exception ex) {
            log.error("Failed to persist security event [{}]: {}", type, ex.getMessage());
        }
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(SecurityEventType type, String actorEmail, String description,
                       String ipAddress, String severity) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                    .eventType(type)
                    .description(description)
                    .ipAddress(ipAddress)
                    .severity(severity)
                    .eventTime(LocalDateTime.now())
                    .actorEmail(actorEmail)
                    .build();
            securityEventRepository.save(event);
            log.info("[SECURITY] type=[{}] actor=[{}] ip=[{}] severity=[{}]",
                    type, actorEmail, ipAddress, severity);
        } catch (Exception ex) {
            log.error("Failed to persist security event [{}]: {}", type, ex.getMessage());
        }
    }
}
