package com.courtai.admin.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Planned maintenance windows declared by the admin.
 *
 * <p>During a MAINTENANCE window the system can restrict logins
 * and display a banner to active users.</p>
 */
@Entity
@Table(
        name = "maintenance_windows",
        indexes = {
                @Index(name = "idx_maint_status",     columnList = "status"),
                @Index(name = "idx_maint_start_time", columnList = "start_time"),
                @Index(name = "idx_maint_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceWindow extends BaseEntity {

    @NotBlank
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Status of the window.
     * Values: SCHEDULED, ACTIVE, COMPLETED, CANCELLED
     */
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "SCHEDULED";

    /** UUID of the admin who created this window. */
    @Column(name = "created_by_admin", length = 36)
    private String createdByAdmin;
}
