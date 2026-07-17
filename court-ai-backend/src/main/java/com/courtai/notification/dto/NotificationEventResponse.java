package com.courtai.notification.dto;

import com.courtai.common.enums.SystemNotificationEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationEventResponse {
    private String uuid;
    private SystemNotificationEvent eventType;
    private String referenceUuid;
    private String module;
    private String status;
    private String errorMessage;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}