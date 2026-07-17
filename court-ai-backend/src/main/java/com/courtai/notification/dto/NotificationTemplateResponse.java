package com.courtai.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationTemplateResponse {
    private String uuid;
    private String code;
    private String title;
    private String subject;
    private String body;
    private String channel;
    private String variables;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}