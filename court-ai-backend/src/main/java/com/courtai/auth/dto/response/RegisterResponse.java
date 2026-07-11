package com.courtai.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO returned after successful registration ({@code POST /auth/register}).
 */
@Getter
@Builder
public class RegisterResponse {
    private final String uuid;
    private final String email;
    private final String message;
    /** Raw email verification token — caller must pass this to /auth/verify-email. */
    private final String emailVerificationToken;
}
