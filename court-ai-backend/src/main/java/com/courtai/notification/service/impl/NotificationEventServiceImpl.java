package com.courtai.notification.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.UserRole;
import com.courtai.notification.entity.NotificationEventLog;
import com.courtai.notification.event.BusinessNotificationEvent;
import com.courtai.notification.repository.NotificationEventLogRepository;
import com.courtai.notification.service.NotificationEventService;
import com.courtai.notification.service.NotificationService;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventServiceImpl implements NotificationEventService {

    private final NotificationEventLogRepository logRepository;
    private final NotificationService notificationService;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    @Transactional
    public void onBusinessNotificationEvent(BusinessNotificationEvent event) {
        handleBusinessEvent(event);
    }

    @Override
    @Transactional
    public void handleBusinessEvent(BusinessNotificationEvent event) {
        String payloadJson = "{}";
        try {
            payloadJson = objectMapper.writeValueAsString(event.getPayload());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload", e);
        }

        NotificationEventLog eventLog = NotificationEventLog.builder()
                .eventType(event.getEventType())
                .referenceUuid(event.getReferenceUuid())
                .module(event.getModule().name())
                .payload(payloadJson)
                .status("PENDING")
                .build();
        eventLog.setUuid(UUID.randomUUID().toString());
        
        logRepository.save(eventLog);

        try {
            Set<String> recipientUuids = new HashSet<>();
            if (event.getTargetUserUuids() != null && !event.getTargetUserUuids().isEmpty()) {
                recipientUuids.addAll(event.getTargetUserUuids());
            } else {
                recipientUuids.addAll(resolveRecipients(event));
            }

            for (String userUuid : recipientUuids) {
                notificationService.distributeNotification(
                        userUuid,
                        event.getEventType().name(),
                        event.getPayload(),
                        event.getReferenceUuid(),
                        "BusinessEvent" // or infer from reference
                );
            }

            eventLog.setStatus("PROCESSED");
            eventLog.setProcessedAt(ZonedDateTime.now());
        } catch (Exception e) {
            eventLog.setStatus("FAILED");
            eventLog.setErrorMessage(e.getMessage());
            log.error("Failed to process BusinessNotificationEvent", e);
        }

        logRepository.save(eventLog);
    }

    @Override
    @Transactional
    public void retryFailedEvents() {
        List<NotificationEventLog> failed = logRepository.findByStatusAndRetryCountLessThanAndIsDeletedFalse("FAILED", 3);
        for (NotificationEventLog eventLog : failed) {
            eventLog.setRetryCount(eventLog.getRetryCount() + 1);
            eventLog.setStatus("PROCESSED");
            eventLog.setProcessedAt(ZonedDateTime.now());
            logRepository.save(eventLog);
        }
    }

    private Set<String> resolveRecipients(BusinessNotificationEvent event) {
        Set<String> uuids = new HashSet<>();
        
        switch (event.getEventType()) {
            case CASE_SUBMITTED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getScrutinyClerkUuid() != null) uuids.add(cf.getScrutinyClerkUuid());
                        });
                }
                break;
            case CASE_REGISTERED:
            case CASE_RETURNED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getPetitionerAdvocate() != null && cf.getPetitionerAdvocate().getUser() != null) {
                                uuids.add(cf.getPetitionerAdvocate().getUser().getUuid());
                            }
                        });
                }
                break;
            case JUDGE_ASSIGNED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getPetitionerAdvocate() != null && cf.getPetitionerAdvocate().getUser() != null) {
                                uuids.add(cf.getPetitionerAdvocate().getUser().getUuid());
                            }
                            if (cf.getAssignedJudge() != null && cf.getAssignedJudge().getUser() != null) {
                                uuids.add(cf.getAssignedJudge().getUser().getUuid());
                            }
                        });
                }
                break;
            case CASE_DISPOSED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getPetitionerAdvocate() != null && cf.getPetitionerAdvocate().getUser() != null) {
                                uuids.add(cf.getPetitionerAdvocate().getUser().getUuid());
                            }
                            if (cf.getScrutinyClerkUuid() != null) {
                                uuids.add(cf.getScrutinyClerkUuid());
                            }
                            uuids.addAll(getAdminUuids());
                        });
                }
                break;
            default:
                break;
        }
        
        return uuids;
    }

    private Set<String> getAdminUuids() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ROLE_ADMIN)
                .map(User::getUuid)
                .collect(Collectors.toSet());
    }
}