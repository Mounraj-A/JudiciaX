package com.courtai.advocate.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.VerificationStatus;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Advocate (Lawyer) profile entity.
 *
 * <p>Advocates represent legal representatives in judicial cases.
 * Each advocate must have a unique Bar Council Registration Number
 * and their account must be verified by an admin before they can file cases.</p>
 *
 * <p>Extended in V16 migration with full professional profile fields.</p>
 */
@Entity
@Table(
        name = "advocate_profiles",
        indexes = {
                @Index(name = "idx_advocate_user_id",      columnList = "user_id"),
                @Index(name = "idx_advocate_bar_number",   columnList = "bar_council_number"),
                @Index(name = "idx_advocate_verification", columnList = "verification_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advocate extends BaseEntity {

    // ── Core Association ──────────────────────────────────────────────────

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ── Bar Council Registration ──────────────────────────────────────────

    /** Unique Bar Council Registration Number (format validated at service layer). */
    @Column(name = "bar_council_number", unique = true, length = 100)
    private String barCouncilNumber;

    /** Date of enrollment at the Bar Council. */
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    /** State Bar Council where the advocate is enrolled. */
    @Column(name = "state_bar_council", length = 200)
    private String stateBarCouncil;

    // ── Professional Details ──────────────────────────────────────────────

    @Column(name = "law_firm", length = 200)
    private String lawFirm;

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "years_of_practice")
    private Integer yearsOfPractice;

    // ── Office Address ────────────────────────────────────────────────────

    @Column(name = "office_address", columnDefinition = "TEXT")
    private String officeAddress;

    @Column(name = "office_city", length = 100)
    private String officeCity;

    @Column(name = "office_state", length = 100)
    private String officeState;

    @Column(name = "office_pincode", length = 10)
    private String officePincode;

    // ── Media Paths ───────────────────────────────────────────────────────

    /** Relative storage path for profile photo. */
    @Column(name = "profile_photo_path", length = 1000)
    private String profilePhotoPath;

    /** Relative storage path for digital signature. */
    @Column(name = "digital_signature_path", length = 1000)
    private String digitalSignaturePath;

    // ── Verification ──────────────────────────────────────────────────────

    /**
     * Legacy boolean verification flag — kept for backward compatibility.
     * Use {@code verificationStatus} for business logic.
     */
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /** UUID of the admin who verified this advocate. */
    @Column(name = "verified_by_uuid", length = 36)
    private String verifiedByUuid;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
