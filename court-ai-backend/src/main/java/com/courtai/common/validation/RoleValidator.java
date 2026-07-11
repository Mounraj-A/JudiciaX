package com.courtai.common.validation;

import com.courtai.common.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates that a given role string maps to a known {@link UserRole} constant.
 *
 * <p>Self-registration through {@code POST /auth/register} is limited to
 * {@code ROLE_ADVOCATE} only. Admin-only creation can use any role.</p>
 */
@Component
public class RoleValidator {

    private static final Set<String> VALID_ROLES = Arrays.stream(UserRole.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static final Set<UserRole> SELF_REGISTRATION_ROLES = Set.of(UserRole.ROLE_ADVOCATE);

    /**
     * Checks whether the role name corresponds to a valid {@link UserRole}.
     *
     * @param roleName the role string (e.g., {@code "ROLE_ADVOCATE"})
     * @return {@code true} if valid
     */
    public boolean isValid(String roleName) {
        if (roleName == null || roleName.isBlank()) return false;
        return VALID_ROLES.contains(roleName.toUpperCase());
    }

    /**
     * Checks whether the role is allowed for self-registration via the public endpoint.
     *
     * @param role the {@link UserRole} to check
     * @return {@code true} if self-registration is permitted for this role
     */
    public boolean isSelfRegistrationAllowed(UserRole role) {
        return role != null && SELF_REGISTRATION_ROLES.contains(role);
    }

    /**
     * Converts a role string to its enum representation safely.
     *
     * @param roleName the role string
     * @return the matching {@link UserRole} or {@code null} if not found
     */
    public UserRole parse(String roleName) {
        try {
            return UserRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
