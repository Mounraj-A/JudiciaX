package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationDeliveryResponse;
import com.courtai.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationHistoryService {
    Page<NotificationResponse> getNotificationHistory(String userUuid, int page, int size);
    Page<NotificationDeliveryResponse> getDeliveryHistory(String userUuid, int page, int size);
    void archiveOldNotifications(int daysOld);
}