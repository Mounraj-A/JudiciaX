package com.courtai.auth.dto.response;

import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full user profile included in login response and GET /users/me.
 */
@Getter
@Builder
public class UserProfileResponse {

    private final String uuid;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final UserRole role;
    private final AccountStatus accountStatus;
    private final Boolean emailVerified;
    private final Boolean mobileVerified;
    private final Integer profileCompletionPercent;
    private final List<String> permissions;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime lastLogin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;
}
