package com.courtai.user.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * User privacy and notification preferences.
 *
 * <p>One-to-one relationship with {@link User}. Created with default values on registration.</p>
 */
@Entity
@Table(name = "user_privacy_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrivacySettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Whether the user opts in to email notifications. */
    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private Boolean emailNotifications = Boolean.TRUE;

    /** Whether the user opts in to SMS notifications. */
    @Column(name = "sms_notifications", nullable = false)
    @Builder.Default
    private Boolean smsNotifications = Boolean.FALSE;

    /** Whether the user prefers dark mode in the UI. */
    @Column(name = "dark_mode", nullable = false)
    @Builder.Default
    private Boolean darkMode = Boolean.FALSE;

    /** Preferred language code (e.g., "en", "ta", "hi"). */
    @Column(name = "language", nullable = false, length = 10)
    @Builder.Default
    private String language = "en";
}
