package com.courtai.admin.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * System configuration key-value pairs managed by the admin.
 *
 * <p>Supports application-level toggles, limits, policy settings,
 * and AI configuration — all editable at runtime without redeployment.</p>
 */
@Entity
@Table(
        name = "system_configurations",
        indexes = {
                @Index(name = "idx_sysconfig_key",        columnList = "config_key"),
                @Index(name = "idx_sysconfig_category",   columnList = "category"),
                @Index(name = "idx_sysconfig_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfiguration extends BaseEntity {

    @NotBlank
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", length = 2000)
    private String configValue;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Category groups related configs.
     * Values: GENERAL, SECURITY, AI, SESSION, UPLOAD, EMAIL, NOTIFICATION, WORKING_HOURS
     */
    @Column(name = "category", length = 50)
    @Builder.Default
    private String category = "GENERAL";

    /** Whether this config can be edited at runtime (false = read-only seed data). */
    @Column(name = "is_editable", nullable = false)
    @Builder.Default
    private Boolean isEditable = Boolean.TRUE;

    /** UUID of the admin who last updated this config. */
    @Column(name = "updated_by_admin", length = 36)
    private String updatedByAdmin;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
