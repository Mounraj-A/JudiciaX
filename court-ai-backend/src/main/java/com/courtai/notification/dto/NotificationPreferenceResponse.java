package com.courtai.notification.dto;

import com.courtai.common.enums.NotificationType;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class NotificationPreferenceResponse {
    private String uuid;
    private NotificationType channel;
    private String notificationType;
    private Boolean isEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}