package com.courtai.advocate.dto;

import com.courtai.common.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for the advocate's own profile.
 * Exposes professional and office information — never exposes entity IDs.
 */
@Getter
@Builder
public class AdvocateProfileResponse {

    private String uuid;
    private String fullName;
    private String email;
    private String phoneNumber;

    // Bar Registration
    private String barCouncilNumber;
    private LocalDate enrollmentDate;
    private String stateBarCouncil;

    // Professional
    private String lawFirm;
    private String specialization;
    private Integer yearsOfPractice;

    // Office
    private String officeAddress;
    private String officeCity;
    private String officeState;
    private String officePincode;

    // Media
    private String profilePhotoPath;
    private String digitalSignaturePath;

    // Verification
    private VerificationStatus verificationStatus;
    private LocalDateTime verifiedAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
