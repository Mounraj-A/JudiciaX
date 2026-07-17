package com.courtai.notification.dto;

import com.courtai.common.enums.DeliveryStatus;
import com.courtai.common.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDeliveryResponse {
    private String uuid;
    private NotificationType channel;
    private DeliveryStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String failureReason;
    private Integer retryCount;
}