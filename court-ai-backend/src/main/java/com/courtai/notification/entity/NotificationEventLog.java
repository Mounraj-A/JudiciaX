package com.courtai.notification.entity;

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
}