package com.courtai.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for resending a mobile OTP ({@code POST /auth/send-otp}). */
@Getter
@Setter
@NoArgsConstructor
public class ResendOtpRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
}
