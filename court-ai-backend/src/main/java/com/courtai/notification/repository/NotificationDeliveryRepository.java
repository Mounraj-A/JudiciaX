package com.courtai.notification.repository;

import com.courtai.common.enums.DeliveryStatus;
import com.courtai.notification.entity.NotificationDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    Optional<NotificationDelivery> findByUuidAndIsDeletedFalse(String uuid);
    List<NotificationDelivery> findByStatusAndRetryCountLessThanAndIsDeletedFalse(DeliveryStatus status, int maxRetries);
    Page<NotificationDelivery> findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(String userUuid, Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(d) FROM NotificationDelivery d WHERE d.recipient.uuid = :userUuid AND d.status != 'READ' AND d.isDeleted = false")
    long countUnreadByRecipientUuid(@org.springframework.data.repository.query.Param("userUuid") String userUuid);
}