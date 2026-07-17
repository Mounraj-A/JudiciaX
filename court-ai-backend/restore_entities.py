import os

files = {
    "src/main/java/com/courtai/notification/entity/NotificationTemplate.java": """package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "default_channel")
    private String defaultChannel;
}""",

    "src/main/java/com/courtai/notification/entity/NotificationDelivery.java": """package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.DeliveryStatus;
import com.courtai.common.enums.NotificationType;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDelivery extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "read_ip")
    private String readIp;

    @Column(name = "read_device")
    private String readDevice;
}""",

    "src/main/java/com/courtai/notification/entity/NotificationPreference.java": """package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.NotificationType;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType channel;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(name = "is_enabled")
    @Builder.Default
    private Boolean isEnabled = true;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;
}""",

    "src/main/java/com/courtai/notification/entity/NotificationEventLog.java": """package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.SystemNotificationEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEventLog extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SystemNotificationEvent eventType;

    @Column(name = "reference_uuid")
    private String referenceUuid;

    @Column(nullable = false)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "processed_at")
    private ZonedDateTime processedAt;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
}""",

    "src/main/java/com/courtai/notification/repository/NotificationTemplateRepository.java": """package com.courtai.notification.repository;

import com.courtai.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCodeAndIsDeletedFalse(String code);
    Optional<NotificationTemplate> findByUuidAndIsDeletedFalse(String uuid);
    boolean existsByCodeAndIsDeletedFalse(String code);
}""",

    "src/main/java/com/courtai/notification/repository/NotificationDeliveryRepository.java": """package com.courtai.notification.repository;

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
}""",

    "src/main/java/com/courtai/notification/repository/NotificationPreferenceRepository.java": """package com.courtai.notification.repository;

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
}""",

    "src/main/java/com/courtai/notification/repository/NotificationEventLogRepository.java": """package com.courtai.notification.repository;

import com.courtai.notification.entity.NotificationEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEventLogRepository extends JpaRepository<NotificationEventLog, Long> {
    List<NotificationEventLog> findByStatusAndRetryCountLessThanAndIsDeletedFalse(String status, int maxRetries);
}""",

    "src/main/java/com/courtai/notification/event/BusinessNotificationEvent.java": """package com.courtai.notification.event;

import com.courtai.common.enums.SystemNotificationEvent;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class BusinessNotificationEvent {
    private final SystemNotificationEvent eventType;
    private final String referenceUuid;
    private final Enum<?> module;
    private final Map<String, Object> payload;
    private final Set<String> targetUserUuids; // Optional, overrides resolution if set
}"""
}

base_dir = "d:/Projects/JudiciaX/court-ai-backend"
for rel_path, content in files.items():
    full_path = os.path.join(base_dir, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Entities, Repositories, and Events restored.")
