import os

files = {
    "src/main/java/com/courtai/notification/dto/NotificationTemplateRequest.java": """package com.courtai.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationTemplateRequest {
    @NotBlank
    private String code;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotBlank
    private String subject;
    
    @NotBlank
    private String body;
    
    private String defaultChannel;
}""",

    "src/main/java/com/courtai/notification/dto/NotificationTemplateResponse.java": """package com.courtai.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationTemplateResponse {
    private String uuid;
    private String code;
    private String name;
    private String description;
    private String subject;
    private String body;
    private String defaultChannel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}""",

    "src/main/java/com/courtai/notification/dto/NotificationPreferenceRequest.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationPreferenceResponse.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationDeliveryResponse.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationResponse.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationSummaryResponse.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationEventResponse.java": """package com.courtai.notification.dto;

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
}""",

    "src/main/java/com/courtai/notification/dto/NotificationSearchRequest.java": """package com.courtai.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSearchRequest {
    private String keyword;
    private Boolean isRead;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}""",

    "src/main/java/com/courtai/notification/dto/UnreadCountResponse.java": """package com.courtai.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnreadCountResponse {
    private long unreadCount;
}"""
}

base_dir = "d:/Projects/JudiciaX/court-ai-backend"
for rel_path, content in files.items():
    full_path = os.path.join(base_dir, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("DTOs restored.")
