package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConfigurationResponse {
    private String uuid;
    private String configKey;
    private String configValue;
    private String description;
    private String category;
    private Boolean isEditable;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
