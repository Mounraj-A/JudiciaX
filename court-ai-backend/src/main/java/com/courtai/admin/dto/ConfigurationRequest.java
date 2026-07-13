package com.courtai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfigurationRequest {

    @NotBlank(message = "Config key is required")
    private String configKey;

    private String configValue;
    private String description;

    /** GENERAL, SECURITY, AI, SESSION, UPLOAD, EMAIL, NOTIFICATION, WORKING_HOURS */
    private String category;

    private Boolean isEditable;

    /** Convenience factory for updating only the configValue field (used by AIAdministrationService). */
    public static ConfigurationRequest withValue(String value) {
        ConfigurationRequest r = new ConfigurationRequest();
        r.configValue = value;
        return r;
    }
}
