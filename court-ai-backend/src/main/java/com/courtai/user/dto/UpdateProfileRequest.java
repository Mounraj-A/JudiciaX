package com.courtai.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for updating an authenticated user's own profile ({@code PUT /users/me}). */
@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 3, max = 200, message = "Full name must be between 3 and 200 characters")
    private String fullName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits starting with 6–9")
    private String phoneNumber;
}
