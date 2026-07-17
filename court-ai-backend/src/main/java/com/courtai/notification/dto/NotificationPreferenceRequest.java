package com.courtai.notification.dto;

import com.courtai.common.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class NotificationPreferenceRequest {
    @NotNull
    private NotificationType channel;
    
    @NotNull
    private String notificationType;
    
    private Boolean isEnabled = true;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
}