package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.PermissionCode;
import com.courtai.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * RBAC Role entity — maps a {@link UserRole} to a set of fine-grained {@link Permission}s.
 *
 * <p>Every user carries a coarse-grained {@link UserRole} for Spring Security authority checks,
 * and this entity links that role to fine-grained permissions for method-level access control.</p>
 */
@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(name = "idx_roles_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    /**
     * The coarse-grained role identifier — maps directly to {@link UserRole}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 30)
    private UserRole name;

    /** Human-readable display name shown in UI (e.g., "Advocate"). */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** Optional description of the role's responsibilities. */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Set of permissions granted to this role.
     * Eagerly loaded because permissions are always needed with the role.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "role_permissions",
            joinColumns        = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Checks whether this role includes the given permission code.
     *
     * @param code the permission to check
     * @return {@code true} if the permission is granted
     */
    public boolean hasPermission(PermissionCode code) {
        return permissions.stream()
                .anyMatch(p -> p.getCode() == code);
    }
}
