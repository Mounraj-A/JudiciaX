package com.courtai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Response DTO returned on successful login ({@code POST /auth/login}).
 */
@Getter
@Builder
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;

    @Builder.Default
    private final String tokenType = "Bearer";

    /** Access token validity in milliseconds. */
    private final Long expiresIn;

    /** Full user profile embedded in the login response. */
    private final UserProfileResponse user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private final LocalDateTime loginTime = LocalDateTime.now();
}
