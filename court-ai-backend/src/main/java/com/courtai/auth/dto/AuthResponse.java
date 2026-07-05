package com.courtai.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO carrying the JWT tokens returned after successful authentication.
 */
@Getter
@Builder
public class AuthResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long expiresIn;
    private final String userUuid;
    private final String email;
    private final String role;
    private final String fullName;
}
