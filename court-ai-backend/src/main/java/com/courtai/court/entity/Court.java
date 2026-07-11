package com.courtai.court.entity;

import com.courtai.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a court institution in the judicial system.
 *
 * <p>Acts as the physical and administrative parent for {@link CourtBench}
 * and {@link CourtRoom}. Every {@link com.courtai.casefile.entity.CaseFile}
 * is filed under a specific court.</p>
 */
@Entity
@Table(
        name = "courts",
        indexes = {
                @Index(name = "idx_court_code",       columnList = "court_code"),
                @Index(name = "idx_court_state",      columnList = "state"),
                @Index(name = "idx_court_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court extends BaseEntity {

    /** Unique short code identifying the court — e.g., "MHC-001", "DLH-002". */
    @NotBlank
    @Column(name = "court_code", nullable = false, unique = true, length = 20)
    private String courtCode;

    /** Full official name of the court. */
    @NotBlank
    @Column(name = "court_name", nullable = false, length = 300)
    private String courtName;

    /**
     * Classification of the court.
     * e.g., HIGH_COURT, DISTRICT_COURT, SESSIONS_COURT, MAGISTRATE_COURT, TRIBUNAL
     */
    @Column(name = "court_type", length = 50)
    private String courtType;

    /** State in which the court is located. */
    @Column(name = "state", length = 100)
    private String state;

    /** District in which the court is located. */
    @Column(name = "district", length = 100)
    private String district;

    /** Full postal address of the court premises. */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /** Phone number for the court registry. */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /** Official email address of the court. */
    @Column(name = "email", length = 150)
    private String email;

    /** Whether this court is currently operational. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
