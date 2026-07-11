package com.courtai.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for email verification ({@code POST /auth/verify-email}). */
@Getter
@Setter
@NoArgsConstructor
public class VerifyEmailRequest {
    @NotBlank(message = "Verification token is required")
    private String token;
}
