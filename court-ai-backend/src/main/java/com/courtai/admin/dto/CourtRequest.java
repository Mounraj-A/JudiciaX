package com.courtai.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request for creating or updating a court. */
@Getter
@NoArgsConstructor
public class CourtRequest {

    @NotBlank(message = "Court code is required")
    @Size(max = 20)
    private String courtCode;

    @NotBlank(message = "Court name is required")
    @Size(max = 300)
    private String courtName;

    /** e.g. HIGH_COURT, DISTRICT_COURT, SESSIONS_COURT, MAGISTRATE_COURT, TRIBUNAL */
    private String courtType;
    private String state;
    private String district;
    private String address;
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private Boolean isActive;
}
