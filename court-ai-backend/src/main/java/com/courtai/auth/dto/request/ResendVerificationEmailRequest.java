package com.courtai.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for resending verification email ({@code POST /auth/resend-verification-email}). */
@Getter
@Setter
@NoArgsConstructor
public class ResendVerificationEmailRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
