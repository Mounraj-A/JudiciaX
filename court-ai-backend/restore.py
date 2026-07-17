import os

files = {
    "src/main/java/com/courtai/notification/mapper/NotificationMapper.java": """package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationEventResponse;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSummaryResponse;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.entity.NotificationEventLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(source = "uuid", target = "uuid")
    NotificationResponse toResponse(Notification entity);

    List<NotificationResponse> toResponseList(List<Notification> entities);

    @Mapping(source = "uuid", target = "uuid")
    NotificationSummaryResponse toSummaryResponse(Notification entity);

    List<NotificationSummaryResponse> toSummaryResponseList(List<Notification> entities);

    @Mapping(source = "uuid", target = "uuid")
    NotificationEventResponse toEventResponse(NotificationEventLog entity);

    List<NotificationEventResponse> toEventResponseList(List<NotificationEventLog> entities);
}""",

    "src/main/java/com/courtai/notification/mapper/NotificationTemplateMapper.java": """package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.entity.NotificationTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationTemplateMapper {

    NotificationTemplate toEntity(NotificationTemplateRequest request);

    @Mapping(source = "uuid", target = "uuid")
    NotificationTemplateResponse toResponse(NotificationTemplate entity);

    List<NotificationTemplateResponse> toResponseList(List<NotificationTemplate> entities);

    void updateEntityFromRequest(NotificationTemplateRequest request, @MappingTarget NotificationTemplate entity);
}""",

    "src/main/java/com/courtai/notification/mapper/NotificationDeliveryMapper.java": """package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationDeliveryResponse;
import com.courtai.notification.entity.NotificationDelivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationDeliveryMapper {

    @Mapping(source = "uuid", target = "uuid")
    NotificationDeliveryResponse toResponse(NotificationDelivery entity);

    List<NotificationDeliveryResponse> toResponseList(List<NotificationDelivery> entities);
}""",

    "src/main/java/com/courtai/notification/mapper/NotificationPreferenceMapper.java": """package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.entity.NotificationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationPreferenceMapper {

    NotificationPreference toEntity(NotificationPreferenceRequest request);

    @Mapping(source = "uuid", target = "uuid")
    NotificationPreferenceResponse toResponse(NotificationPreference entity);

    List<NotificationPreferenceResponse> toResponseList(List<NotificationPreference> entities);

    void updateEntityFromRequest(NotificationPreferenceRequest request, @MappingTarget NotificationPreference entity);
}""",

    "src/main/java/com/courtai/notification/service/NotificationTemplateService.java": """package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;

import java.util.List;
import java.util.Map;

public interface NotificationTemplateService {
    NotificationTemplateResponse createTemplate(NotificationTemplateRequest request);
    NotificationTemplateResponse updateTemplate(String uuid, NotificationTemplateRequest request);
    NotificationTemplateResponse getTemplateByUuid(String uuid);
    List<NotificationTemplateResponse> getAllTemplates();
    void deleteTemplate(String uuid);
    
    /**
     * Renders a template body by replacing placeholders with variables.
     */
    String render(String templateCode, Map<String, Object> variables);
    String renderSubject(String templateCode, Map<String, Object> variables);
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationTemplateServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.entity.NotificationTemplate;
import com.courtai.notification.mapper.NotificationTemplateMapper;
import com.courtai.notification.repository.NotificationTemplateRepository;
import com.courtai.notification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository repository;
    private final NotificationTemplateMapper mapper;

    @Override
    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        if (repository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new IllegalArgumentException("Template code already exists");
        }
        NotificationTemplate template = mapper.toEntity(request);
        template.setUuid(UUID.randomUUID().toString());
        return mapper.toResponse(repository.save(template));
    }

    @Override
    @Transactional
    public NotificationTemplateResponse updateTemplate(String uuid, NotificationTemplateRequest request) {
        NotificationTemplate template = getEntityByUuid(uuid);
        if (!template.getCode().equals(request.getCode()) && repository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new IllegalArgumentException("Template code already exists");
        }
        mapper.updateEntityFromRequest(request, template);
        return mapper.toResponse(repository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getTemplateByUuid(String uuid) {
        return mapper.toResponse(getEntityByUuid(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getAllTemplates() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    @Transactional
    public void deleteTemplate(String uuid) {
        NotificationTemplate template = getEntityByUuid(uuid);
        template.setIsDeleted(true);
        repository.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    public String render(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = repository.findByCodeAndIsDeletedFalse(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "code", templateCode));
        return renderText(template.getBody(), variables);
    }

    @Override
    @Transactional(readOnly = true)
    public String renderSubject(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = repository.findByCodeAndIsDeletedFalse(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "code", templateCode));
        return renderText(template.getSubject(), variables);
    }

    private String renderText(String text, Map<String, Object> variables) {
        if (text == null) return "";
        if (variables == null || variables.isEmpty()) return text;
        String result = text;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    private NotificationTemplate getEntityByUuid(String uuid) {
        return repository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "uuid", uuid));
    }
}""",

    "src/main/java/com/courtai/notification/service/NotificationPreferenceService.java": """package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;

import java.util.List;

public interface NotificationPreferenceService {
    NotificationPreferenceResponse updatePreference(String userUuid, NotificationPreferenceRequest request);
    List<NotificationPreferenceResponse> getUserPreferences(String userUuid);
    boolean isDeliveryEnabled(String userUuid, com.courtai.common.enums.NotificationType channel, String notificationType);
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationPreferenceServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.common.enums.NotificationType;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.entity.NotificationPreference;
import com.courtai.notification.mapper.NotificationPreferenceMapper;
import com.courtai.notification.repository.NotificationPreferenceRepository;
import com.courtai.notification.service.NotificationPreferenceService;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;
    private final NotificationPreferenceMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationPreferenceResponse updatePreference(String userUuid, NotificationPreferenceRequest request) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        Optional<NotificationPreference> existing = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, request.getChannel(), request.getNotificationType());

        NotificationPreference preference;
        if (existing.isPresent()) {
            preference = existing.get();
            mapper.updateEntityFromRequest(request, preference);
        } else {
            preference = mapper.toEntity(request);
            preference.setUuid(UUID.randomUUID().toString());
            preference.setUser(user);
        }
        return mapper.toResponse(repository.save(preference));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getUserPreferences(String userUuid) {
        return mapper.toResponseList(repository.findByUserUuidAndIsDeletedFalse(userUuid));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDeliveryEnabled(String userUuid, NotificationType channel, String notificationType) {
        Optional<NotificationPreference> specific = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, channel, notificationType);
        if (specific.isPresent()) {
            return checkQuietHours(specific.get());
        }
        // Fallback to ALL
        Optional<NotificationPreference> all = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, channel, "ALL");
        if (all.isPresent()) {
            return checkQuietHours(all.get());
        }
        return true; // Default enabled
    }

    private boolean checkQuietHours(NotificationPreference pref) {
        if (!pref.getIsEnabled()) return false;
        if (pref.getQuietHoursStart() == null || pref.getQuietHoursEnd() == null) return true;
        
        LocalTime now = LocalTime.now();
        LocalTime start = pref.getQuietHoursStart();
        LocalTime end = pref.getQuietHoursEnd();

        if (start.isBefore(end)) {
            // Quiet hours e.g., 14:00 to 16:00
            if (now.isAfter(start) && now.isBefore(end)) return false;
        } else {
            // Quiet hours cross midnight e.g., 22:00 to 07:00
            if (now.isAfter(start) || now.isBefore(end)) return false;
        }
        return true;
    }
}""",

    "src/main/java/com/courtai/notification/service/NotificationDeliveryService.java": """package com.courtai.notification.service;

import com.courtai.notification.entity.Notification;

public interface NotificationDeliveryService {
    void deliver(Notification notification, com.courtai.common.enums.NotificationType channel);
    void retryFailedDeliveries();
    void markAsRead(String deliveryUuid, String ipAddress, String userAgent);
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationDeliveryServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.common.enums.DeliveryStatus;
import com.courtai.common.enums.NotificationType;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.entity.NotificationDelivery;
import com.courtai.notification.repository.NotificationDeliveryRepository;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.notification.service.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void deliver(Notification notification, NotificationType channel) {
        NotificationDelivery delivery = NotificationDelivery.builder()
                .uuid(UUID.randomUUID().toString())
                .notification(notification)
                .recipient(notification.getRecipient())
                .channel(channel)
                .status(DeliveryStatus.PROCESSING)
                .build();
        
        deliveryRepository.save(delivery);

        try {
            // Mock integration logic for Phase 2 readiness
            if (channel == NotificationType.IN_APP) {
                delivery.setStatus(DeliveryStatus.DELIVERED);
                delivery.setDeliveredAt(ZonedDateTime.now());
            } else {
                // Email, SMS, Push, Webhook providers logic here
                // Simulate success
                delivery.setStatus(DeliveryStatus.DELIVERED);
                delivery.setDeliveredAt(ZonedDateTime.now());
            }
        } catch (Exception e) {
            delivery.setStatus(DeliveryStatus.FAILED);
            delivery.setFailureReason(e.getMessage());
            log.error("Failed to deliver notification {} via {}", notification.getUuid(), channel, e);
        }
        
        delivery.setSentAt(ZonedDateTime.now());
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void retryFailedDeliveries() {
        List<NotificationDelivery> failed = deliveryRepository.findByStatusAndRetryCountLessThanAndIsDeletedFalse(DeliveryStatus.FAILED, 3);
        for (NotificationDelivery delivery : failed) {
            delivery.setRetryCount(delivery.getRetryCount() + 1);
            delivery.setStatus(DeliveryStatus.PROCESSING);
            try {
                // Mock retry logic
                delivery.setStatus(DeliveryStatus.DELIVERED);
                delivery.setDeliveredAt(ZonedDateTime.now());
                delivery.setFailureReason(null);
            } catch (Exception e) {
                delivery.setStatus(DeliveryStatus.FAILED);
                delivery.setFailureReason(e.getMessage());
            }
            deliveryRepository.save(delivery);
        }
    }

    @Override
    @Transactional
    public void markAsRead(String deliveryUuid, String ipAddress, String userAgent) {
        NotificationDelivery delivery = deliveryRepository.findByUuidAndIsDeletedFalse(deliveryUuid)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationDelivery", "uuid", deliveryUuid));
        
        delivery.setStatus(DeliveryStatus.READ);
        delivery.setReadAt(ZonedDateTime.now());
        delivery.setReadIp(ipAddress);
        delivery.setReadDevice(userAgent);
        deliveryRepository.save(delivery);

        Notification notification = delivery.getNotification();
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.common.enums.NotificationType;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.repository.NotificationDeliveryRepository;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.notification.service.NotificationDeliveryService;
import com.courtai.notification.service.NotificationPreferenceService;
import com.courtai.notification.service.NotificationService;
import com.courtai.notification.service.NotificationTemplateService;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final NotificationTemplateService templateService;
    private final NotificationPreferenceService preferenceService;
    private final NotificationDeliveryService notificationDeliveryService;

    @Override
    @Transactional
    public void sendInAppNotification(String recipientUuid, String title, String message,
                                      String referenceUuid, String referenceType) {
        log.info("[NOTIFICATION] Legacy Support: IN_APP -> recipient={} title='{}' ref={}({})",
                recipientUuid, title, referenceType, referenceUuid);
                
        User recipient = userRepository.findByUuidAndIsDeletedFalse(recipientUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", recipientUuid));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .notificationType(NotificationType.IN_APP)
                .title(title)
                .message(message)
                .referenceUuid(referenceUuid)
                .referenceType(referenceType)
                .build();
        
        notificationRepository.save(notification);
        notificationDeliveryService.deliver(notification, NotificationType.IN_APP);
    }

    @Override
    @Transactional
    public void distributeNotification(String recipientUuid, String templateCode, Map<String, Object> variables,
                                       String referenceUuid, String referenceType) {
                                           
        User recipient = userRepository.findByUuidAndIsDeletedFalse(recipientUuid).orElse(null);
        if (recipient == null) {
            log.warn("Cannot distribute notification. Recipient not found: {}", recipientUuid);
            return;
        }

        // Render template
        String title = templateService.renderSubject(templateCode, variables);
        String body = templateService.render(templateCode, variables);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .notificationType(NotificationType.IN_APP) // default master record
                .title(title)
                .message(body)
                .referenceUuid(referenceUuid)
                .referenceType(referenceType)
                .build();
        
        notification = notificationRepository.save(notification);

        // Check preferences and deliver to all enabled channels
        for (NotificationType channel : NotificationType.values()) {
            if (preferenceService.isDeliveryEnabled(recipientUuid, channel, templateCode)) {
                notificationDeliveryService.deliver(notification, channel);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userUuid) {
        return deliveryRepository.countUnreadByRecipientUuid(userUuid);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userUuid) {
        // Find all unread notifications for this user and mark as read
        List<Notification> unread = notificationRepository.findByRecipientUuidAndIsReadFalse(userUuid);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void deleteNotification(String uuid) {
        Notification notification = notificationRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "uuid", uuid));
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }
}""",

    "src/main/java/com/courtai/notification/service/NotificationEventService.java": """package com.courtai.notification.service;

import com.courtai.notification.event.BusinessNotificationEvent;

public interface NotificationEventService {
    void handleBusinessEvent(BusinessNotificationEvent event);
    void retryFailedEvents();
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationEventServiceImpl.java": """package com.courtai.notification.service.impl;

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
                .uuid(UUID.randomUUID().toString())
                .eventType(event.getEventType())
                .referenceUuid(event.getReferenceUuid())
                .module(event.getModule().name())
                .payload(payloadJson)
                .status("PENDING")
                .build();
        
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
                            if (cf.getAssignedClerk() != null) uuids.add(cf.getAssignedClerk().getUuid());
                        });
                }
                break;
            case CASE_REGISTERED:
            case CASE_RETURNED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getAdvocate() != null) uuids.add(cf.getAdvocate().getUuid());
                        });
                }
                break;
            case JUDGE_ASSIGNED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getAdvocate() != null) uuids.add(cf.getAdvocate().getUuid());
                            if (cf.getAssignedJudge() != null) uuids.add(cf.getAssignedJudge().getUuid());
                        });
                }
                break;
            case CASE_DISPOSED:
                if (event.getReferenceUuid() != null) {
                    caseFileRepository.findByUuidAndIsDeletedFalse(event.getReferenceUuid())
                        .ifPresent(cf -> {
                            if (cf.getAdvocate() != null) uuids.add(cf.getAdvocate().getUuid());
                            if (cf.getAssignedClerk() != null) uuids.add(cf.getAssignedClerk().getUuid());
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
}""",

    "src/main/java/com/courtai/notification/service/NotificationHistoryService.java": """package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationDeliveryResponse;
import com.courtai.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationHistoryService {
    Page<NotificationResponse> getNotificationHistory(String userUuid, int page, int size);
    Page<NotificationDeliveryResponse> getDeliveryHistory(String userUuid, int page, int size);
    void archiveOldNotifications(int daysOld);
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationHistoryServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.common.enums.DeliveryStatus;
import com.courtai.notification.dto.NotificationDeliveryResponse;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.entity.NotificationDelivery;
import com.courtai.notification.mapper.NotificationDeliveryMapper;
import com.courtai.notification.mapper.NotificationMapper;
import com.courtai.notification.repository.NotificationDeliveryRepository;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.notification.service.NotificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationDeliveryMapper deliveryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationHistory(String userUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> entityPage = notificationRepository.findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(userUuid, pageable);
        return entityPage.map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDeliveryResponse> getDeliveryHistory(String userUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDelivery> entityPage = deliveryRepository.findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(userUuid, pageable);
        return entityPage.map(deliveryMapper::toResponse);
    }

    @Override
    @Transactional
    public void archiveOldNotifications(int daysOld) {
        ZonedDateTime cutoff = ZonedDateTime.now().minusDays(daysOld);
    }
}""",

    "src/main/java/com/courtai/notification/service/NotificationSearchService.java": """package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import org.springframework.data.domain.Page;

public interface NotificationSearchService {
    Page<NotificationResponse> search(String userUuid, NotificationSearchRequest request);
}""",

    "src/main/java/com/courtai/notification/service/impl/NotificationSearchServiceImpl.java": """package com.courtai.notification.service.impl;

import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.mapper.NotificationMapper;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.notification.service.NotificationSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSearchServiceImpl implements NotificationSearchService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> search(String userUuid, NotificationSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(), 
                request.getSize(), 
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("recipient").get("uuid"), userUuid));
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likePattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("message")), likePattern)
                ));
            }
            if (request.getIsRead() != null) {
                predicates.add(cb.equal(root.get("isRead"), request.getIsRead()));
            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Notification> entityPage = notificationRepository.findAll(spec, pageable);
        return entityPage.map(notificationMapper::toResponse);
    }
}""",

    "src/main/java/com/courtai/notification/controller/NotificationController.java": """package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.UnreadCountResponse;
import com.courtai.notification.service.NotificationHistoryService;
import com.courtai.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationHistoryService notificationHistoryService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @Operation(summary = "Get user notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(notificationHistoryService.getNotificationHistory(userUuid, page, size));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount() {
        String userUuid = authorizationService.getCurrentUserUuid();
        long count = notificationService.getUnreadCount(userUuid);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead() {
        String userUuid = authorizationService.getCurrentUserUuid();
        notificationService.markAllAsRead(userUuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable String uuid) {
        notificationService.deleteNotification(uuid);
        return ResponseEntity.noContent().build();
    }
}""",

    "src/main/java/com/courtai/notification/controller/NotificationTemplateController.java": """package com.courtai.notification.controller;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notification-templates")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Admin endpoints for notification templates")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    @PostMapping
    @Operation(summary = "Create a new notification template")
    public ResponseEntity<NotificationTemplateResponse> createTemplate(@Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update an existing notification template")
    public ResponseEntity<NotificationTemplateResponse> updateTemplate(
            @PathVariable String uuid,
            @Valid @RequestBody NotificationTemplateRequest request) {
        return ResponseEntity.ok(templateService.updateTemplate(uuid, request));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a notification template by UUID")
    public ResponseEntity<NotificationTemplateResponse> getTemplate(@PathVariable String uuid) {
        return ResponseEntity.ok(templateService.getTemplateByUuid(uuid));
    }

    @GetMapping
    @Operation(summary = "Get all notification templates")
    public ResponseEntity<List<NotificationTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a notification template")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String uuid) {
        templateService.deleteTemplate(uuid);
        return ResponseEntity.noContent().build();
    }
}""",

    "src/main/java/com/courtai/notification/controller/NotificationPreferenceController.java": """package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Endpoints for managing user notification preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @Operation(summary = "Get user notification preferences")
    public ResponseEntity<List<NotificationPreferenceResponse>> getPreferences() {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(preferenceService.getUserPreferences(userUuid));
    }

    @PutMapping
    @Operation(summary = "Update a notification preference")
    public ResponseEntity<NotificationPreferenceResponse> updatePreference(
            @Valid @RequestBody NotificationPreferenceRequest request) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(preferenceService.updatePreference(userUuid, request));
    }
}""",

    "src/main/java/com/courtai/notification/controller/NotificationEventController.java": """package com.courtai.notification.controller;

import com.courtai.notification.service.NotificationEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notification-events")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Admin endpoints for managing notification events")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NotificationEventController {

    private final NotificationEventService eventService;

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notification events")
    public ResponseEntity<Void> retryFailedEvents() {
        eventService.retryFailedEvents();
        return ResponseEntity.ok().build();
    }
}""",

    "src/main/java/com/courtai/notification/controller/NotificationSearchController.java": """package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import com.courtai.notification.service.NotificationSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/search")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Advanced search for notifications")
public class NotificationSearchController {

    private final NotificationSearchService searchService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Search notifications with advanced filters")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            @RequestBody NotificationSearchRequest request) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(searchService.search(userUuid, request));
    }
}"""
}

base_dir = "d:/Projects/JudiciaX/court-ai-backend"
for rel_path, content in files.items():
    full_path = os.path.join(base_dir, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Files restored and updated.")
