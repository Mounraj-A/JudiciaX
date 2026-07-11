package com.courtai.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for updating privacy settings ({@code PUT /users/me/privacy-settings}). */
@Getter
@Setter
@NoArgsConstructor
public class PrivacySettingsRequest {
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean darkMode;
    private String language;
}
