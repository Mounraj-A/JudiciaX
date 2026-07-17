package com.courtai.notification.repository;

import com.courtai.common.enums.NotificationType;
import com.courtai.notification.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    List<NotificationPreference> findByUserUuidAndIsDeletedFalse(String userUuid);
    Optional<NotificationPreference> findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(String userUuid, NotificationType channel, String notificationType);
}