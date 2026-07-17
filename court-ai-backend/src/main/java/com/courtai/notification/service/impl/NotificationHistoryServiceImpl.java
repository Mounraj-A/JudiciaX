package com.courtai.notification.service.impl;

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
}