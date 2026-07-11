package com.courtai.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for token rotation ({@code POST /auth/refresh}).
 *
 * <p>The old refresh token is invalidated; this response contains a fresh pair.</p>
 */
@Getter
@Builder
public class RefreshTokenResponse {
    private final String accessToken;
    private final String refreshToken;
    @Builder.Default
    private final String tokenType = "Bearer";
    private final Long expiresIn;
}
