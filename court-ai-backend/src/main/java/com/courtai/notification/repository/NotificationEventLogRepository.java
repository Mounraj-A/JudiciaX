package com.courtai.notification.repository;

import com.courtai.notification.entity.NotificationEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventLogRepository extends JpaRepository<NotificationEventLog, Long> {
    List<NotificationEventLog> findByStatusAndRetryCountLessThanAndIsDeletedFalse(String status, int maxRetries);
}