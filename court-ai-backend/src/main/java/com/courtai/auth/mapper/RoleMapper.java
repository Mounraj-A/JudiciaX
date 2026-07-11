package com.courtai.auth.mapper;

import com.courtai.auth.dto.PermissionSummaryResponse;
import com.courtai.auth.dto.RoleResponse;
import com.courtai.auth.dto.RoleSummaryResponse;
import com.courtai.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper converting {@link Role} entity to RBAC response DTOs.
 *
 * <p>Uses {@code componentModel = "spring"} so Spring injects the implementation.
 * Uses constructor injection via {@code injectionStrategy = CONSTRUCTOR}.</p>
 */
@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    /**
     * Maps a Role entity to a full RoleResponse (with embedded permissions).
     */
    @Mapping(target = "roleCode",    expression = "java(role.getName() != null ? role.getName().name() : null)")
    @Mapping(target = "isSystemRole", expression = "java(isSystemRole(role))")
    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toResponse(Role role);

    /**
     * Maps a Role entity to a lightweight summary (no permissions).
     */
    @Mapping(target = "roleCode", expression = "java(role.getName() != null ? role.getName().name() : null)")
    RoleSummaryResponse toSummary(Role role);

    /**
     * Maps a list of Role entities to a list of summaries.
     */
    List<RoleSummaryResponse> toSummaryList(List<Role> roles);

    /**
     * Determines whether a role is a system role (cannot be deleted).
     * All four base roles (ADMIN, JUDGE, CLERK, ADVOCATE) are system roles.
     */
    default boolean isSystemRole(Role role) {
        return role.getName() != null;
    }
}
