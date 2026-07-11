package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.PermissionCode;
import jakarta.persistence.*;
import lombok.*;

/**
 * RBAC Permission entity — represents a single fine-grained access right.
 *
 * <p>Permissions are grouped into modules (e.g., CASE_MANAGEMENT, USER_MANAGEMENT)
 * and assigned to {@link Role}s via the {@code role_permissions} join table.</p>
 *
 * <p>Used with {@code @PreAuthorize("hasAuthority('CREATE_CASE')")} at service/controller level.</p>
 */
@Entity
@Table(
        name = "permissions",
        indexes = {
                @Index(name = "idx_permissions_code",   columnList = "code"),
                @Index(name = "idx_permissions_module", columnList = "module")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    /**
     * The unique permission code — mirrors a {@link PermissionCode} enum value.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 60)
    private PermissionCode code;

    /** Human-readable name (e.g., "Create Case"). */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Detailed description of what this permission allows. */
    @Column(name = "description", length = 500)
    private String description;

    /** Module this permission belongs to (e.g., "CASE_MANAGEMENT", "USER_MANAGEMENT"). */
    @Column(name = "module", nullable = false, length = 100)
    private String module;
}
