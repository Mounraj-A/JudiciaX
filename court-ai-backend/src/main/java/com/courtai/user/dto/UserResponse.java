package com.courtai.user.dto;

import com.courtai.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

/**
 * Read-only DTO for exposing user data in API responses.
 *
 * <p>NEVER exposes the internal Long id or the password hash.</p>
 */
@Getter
@Builder
public class UserResponse {

    private final String uuid;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final UserRole role;
    private final Boolean isActive;
    private final Boolean isEmailVerified;
}
