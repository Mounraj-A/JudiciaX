package com.courtai.auth.mapper;

import com.courtai.auth.dto.PermissionResponse;
import com.courtai.auth.dto.PermissionSummaryResponse;
import com.courtai.auth.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper converting {@link Permission} entity to RBAC response DTOs.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Maps a Permission entity to a full PermissionResponse.
     * {@code isActive} is the inverse of {@code isDeleted}.
     */
    @Mapping(target = "permissionCode", expression = "java(permission.getCode() != null ? permission.getCode().name() : null)")
    @Mapping(target = "permissionName",  source = "name")
    @Mapping(target = "isActive",        expression = "java(permission.getIsDeleted() == null || !permission.getIsDeleted())")
    PermissionResponse toResponse(Permission permission);

    /**
     * Maps a Permission entity to a lightweight summary embedded inside RoleResponse.
     */
    @Mapping(target = "permissionCode", expression = "java(permission.getCode() != null ? permission.getCode().name() : null)")
    @Mapping(target = "permissionName",  source = "name")
    PermissionSummaryResponse toSummary(Permission permission);

    /**
     * Maps a list of Permission entities to PermissionResponse records.
     */
    List<PermissionResponse> toResponseList(List<Permission> permissions);

    /**
     * Maps a Set of Permission entities to PermissionSummaryResponse records (for role embedding).
     */
    Set<PermissionSummaryResponse> toSummarySet(Set<Permission> permissions);
}
