package com.courtai.admin.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

/**
 * System-wide announcements broadcast by admin to specific roles or all users.
 */
@Entity
@Table(
        name = "system_announcements",
        indexes = {
                @Index(name = "idx_announcement_target",     columnList = "target_role"),
                @Index(name = "idx_announcement_active",     columnList = "is_active"),
                @Index(name = "idx_announcement_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemAnnouncement extends BaseEntity {

    @NotBlank
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @NotBlank
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Priority level.
     * Values: LOW, MEDIUM, HIGH, CRITICAL
     */
    @Column(name = "priority", length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    /**
     * Target audience role (null = all roles).
     * Values: ROLE_JUDGE, ROLE_CLERK, ROLE_ADVOCATE, ROLE_ADMIN, ALL
     */
    @Column(name = "target_role", length = 30)
    @Builder.Default
    private String targetRole = "ALL";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.FALSE;

    /** UUID of the admin who created this announcement. */
    @Column(name = "created_by_admin", length = 36)
    private String createdByAdmin;
}
