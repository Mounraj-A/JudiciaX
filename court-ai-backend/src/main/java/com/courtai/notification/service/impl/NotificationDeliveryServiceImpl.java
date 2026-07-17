package com.courtai.notification.service.impl;

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
                .notification(notification)
                .recipient(notification.getRecipient())
                .channel(channel)
                .status(DeliveryStatus.PROCESSING)
                .build();
        delivery.setUuid(UUID.randomUUID().toString());
        
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
}