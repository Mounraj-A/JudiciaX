package com.courtai.notification.service.impl;

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
}