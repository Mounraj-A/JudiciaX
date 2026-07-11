package com.courtai.advocate.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for updating the advocate's own profile.
 * All fields are optional — only non-null fields are applied.
 */
@Getter
@Setter
public class UpdateAdvocateProfileRequest {

    @Pattern(
            regexp = "^[A-Z]{3}/[A-Z]{3}/\\d{4}/\\d{5}$",
            message = "Bar Council Number must follow format: STATE/BAR/YEAR/SERIAL (e.g. MH/MUM/2020/12345)"
    )
    private String barCouncilNumber;

    private LocalDate enrollmentDate;

    @Size(max = 200)
    private String stateBarCouncil;

    @Size(max = 200)
    private String lawFirm;

    @Size(max = 200)
    private String specialization;

    private Integer yearsOfPractice;

    private String officeAddress;

    @Size(max = 100)
    private String officeCity;

    @Size(max = 100)
    private String officeState;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String officePincode;
}
