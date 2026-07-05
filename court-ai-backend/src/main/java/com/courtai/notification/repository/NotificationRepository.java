package com.courtai.notification.repository;

import com.courtai.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for user notifications. */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(String userUuid, Pageable pageable);
    long countByRecipientUuidAndIsReadFalseAndIsDeletedFalse(String userUuid);
}
