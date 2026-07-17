package com.courtai.notification.dto;

import com.courtai.common.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private String uuid;
    private String title;
    private String message;
    private NotificationType notificationType;
    private String referenceUuid;
    private String referenceType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}