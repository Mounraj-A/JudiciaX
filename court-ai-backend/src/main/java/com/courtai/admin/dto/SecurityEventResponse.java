package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Security event entry shown in the admin security monitoring panel. */
@Getter
@Builder
public class SecurityEventResponse {
    private String uuid;
    private String eventType;
    private String userUuid;
    private String userEmail;
    private String ipAddress;
    private String browser;
    private String device;
    private String status;
    private String details;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt;
}
