package com.courtai.notification.service;

import com.courtai.notification.entity.Notification;

public interface NotificationDeliveryService {
    void deliver(Notification notification, com.courtai.common.enums.NotificationType channel);
    void retryFailedDeliveries();
    void markAsRead(String deliveryUuid, String ipAddress, String userAgent);
}