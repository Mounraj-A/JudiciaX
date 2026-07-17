package com.courtai.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationTemplateRequest {
    @NotBlank
    private String code;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String subject;
    
    @NotBlank
    private String body;
    
    @NotBlank
    private String channel;
    
    private String variables;
}