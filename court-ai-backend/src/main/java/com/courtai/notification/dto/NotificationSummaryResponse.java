package com.courtai.notification.dto;

import com.courtai.common.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSummaryResponse {
    private String uuid;
    private String title;
    private NotificationType notificationType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}