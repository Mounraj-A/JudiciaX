package com.courtai.advocate.dto;

import com.courtai.common.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Notification item response for the Advocate Portal.
 */
@Getter
@Builder
public class NotificationResponse {

    private String uuid;
    private NotificationType notificationType;
    private String title;
    private String message;
    private String referenceUuid;
    private String referenceType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
