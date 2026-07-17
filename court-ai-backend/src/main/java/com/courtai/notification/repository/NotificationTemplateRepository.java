package com.courtai.notification.repository;

import com.courtai.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCodeAndIsDeletedFalse(String code);
    Optional<NotificationTemplate> findByUuidAndIsDeletedFalse(String uuid);
    boolean existsByCodeAndIsDeletedFalse(String code);
}