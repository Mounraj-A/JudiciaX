package com.courtai.casemanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CaseWizardDraftRequest {
    
    // Existing Case UUID if resuming draft
    private String caseUuid;

    // --- Step 1: Where to File ---
    private String state;
    private String district;
    private String court;
    private String courtType;
    private String bench;
    private String courtHall;
    private String caseNature; // Case Nature (Civil/Criminal)
    private String caseType; // Case Type Code (e.g. CRIM, CIVIL)
    private String filingMode;

    // --- Step 2: Case Info ---
    private String caseTitle;
    private String causeTitle;
    private String caseCategoryUuid;
    private String subject;
    private String natureOfSuit;
    private String shortDescription;
    private String detailedDescription;
    private String causeOfAction;
    private LocalDate dateOfCauseOfAction;
    private String reliefSought;

    // --- Step 3, 4, 9: Parties ---
    private PartyDto petitioner;
    private PartyDto respondent;
    private List<PartyDto> additionalParties;

    // --- Step 5: Extra Info (Legal Info) ---
    private String policeStation;
    private String firNumber;

    // --- Step 7: Subordinate Court ---
    private String subordinateCourt;
    private String judgeName;
    private String caseNumber;
    private Integer year;
    private String cnrNumber;
    private LocalDate judgmentDate;

    // --- Step 8: Act & Section ---
    private List<ActSectionDto> caseActs;

    // --- Nested DTOs ---

    @Data
    @NoArgsConstructor
    public static class PartyDto {
        private String partyType;
        private String partyCategory; // INDIVIDUAL / ORGANIZATION
        private String name;
        private String alias;
        private String gender;
        private LocalDate dob;
        private Integer age;
        private String occupation;
        private String aadhaar;
        private String pan;
        private String mobile;
        private String email;
        private String address;
        private String state;
        private String district;
        private String pincode;
        private String representativeName;
        // From Step 17 Schema
        private String passportNumber;
        private String nationality;
        private String additionalAddress;
        private String otherInformation;
    }

    @Data
    @NoArgsConstructor
    public static class ActSectionDto {
        private String act;
        private String section;
        private String article;
    }
}
