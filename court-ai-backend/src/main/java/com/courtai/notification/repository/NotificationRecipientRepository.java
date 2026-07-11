package com.courtai.notification.repository;

import com.courtai.notification.entity.NotificationRecipient;
import com.courtai.common.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for NotificationRecipient — per-user, per-channel delivery tracking. */
@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

    List<NotificationRecipient> findByNotificationIdAndIsDeletedFalse(Long notificationId);

    List<NotificationRecipient> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);

    Page<NotificationRecipient> findByUserIdAndDeliveryStatusAndIsDeletedFalse(
            Long userId, DeliveryStatus deliveryStatus, Pageable pageable);

    Optional<NotificationRecipient> findByNotificationIdAndUserIdAndDeliveryChannel(
            Long notificationId, Long userId, String deliveryChannel);

    long countByUserIdAndDeliveryStatusAndIsDeletedFalse(Long userId, DeliveryStatus deliveryStatus);

    Optional<NotificationRecipient> findByUuidAndIsDeletedFalse(String uuid);
}
