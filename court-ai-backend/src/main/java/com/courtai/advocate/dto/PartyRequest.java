package com.courtai.advocate.dto;

import com.courtai.common.enums.PartyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class PartyRequest {
    @NotBlank(message = "Party name is required")
    private String partyName;

    @NotNull(message = "Party type is required")
    private PartyType partyType;

    private Boolean isPrimary = false;
    private String partyCategory; // INDIVIDUAL, ORGANIZATION
    private String gender;
    private LocalDate dateOfBirth;
    private String aadhaarNumber;
    private String phoneNumber;
    private String email;
    private String partyAddress;
    private String district;
    private String state;
    private String pinCode;
    private String occupation;
    private String representativeName;
}
