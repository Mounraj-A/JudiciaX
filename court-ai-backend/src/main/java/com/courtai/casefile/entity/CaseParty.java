package com.courtai.casefile.entity;

import com.courtai.advocate.entity.Advocate;
import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.PartyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Represents a party involved in a judicial case.
 *
 * <p>A case may have multiple parties of different types (petitioner,
 * respondent, intervenor, witness, etc.). This entity tracks each
 * party individually with their contact information and legal representation.</p>
 */
@Entity
@Table(
        name = "case_parties",
        indexes = {
                @Index(name = "idx_party_case_id",    columnList = "case_id"),
                @Index(name = "idx_party_type",       columnList = "party_type"),
                @Index(name = "idx_party_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseParty extends BaseEntity {

    /** The case this party belongs to. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /** Role of this party in the case. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false, length = 30)
    private PartyType partyType;

    /** Full legal name of the party. */
    @NotBlank
    @Column(name = "party_name", nullable = false, length = 300)
    private String partyName;

    /** Residential or business address. */
    @Column(name = "party_address", columnDefinition = "TEXT")
    private String partyAddress;

    /** Contact phone number of the party. */
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    /** Email address of the party. */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Advocate representing this party.
     * Nullable — a party may appear in person.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advocate_id")
    private Advocate advocate;

    /** Whether this is the primary party for their role in the case. */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = Boolean.FALSE;

    // --- Extended Wizard Fields ---

    @Column(name = "party_category", length = 50)
    private String partyCategory; // e.g., INDIVIDUAL, ORGANIZATION

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "aadhaar_number", length = 100)
    private String aadhaarNumber;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pin_code", length = 20)
    private String pinCode;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "representative_name", length = 200)
    private String representativeName;

    // --- Added for 17-step wizard ---

    @Column(name = "alias_name", length = 200)
    private String aliasName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "pan_number", length = 50)
    private String panNumber;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "additional_address", columnDefinition = "TEXT")
    private String additionalAddress;

    @Column(name = "other_information", columnDefinition = "TEXT")
    private String otherInformation;
}
