package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Mobile OTP entity for phone number verification.
 *
 * <p>A 6-digit OTP is generated and its BCrypt hash is stored here.
 * The raw OTP is returned via the API response (simulating SMS delivery).</p>
 *
 * <p>After {@link com.courtai.common.constants.AppConstants#MAX_OTP_ATTEMPTS} failed attempts,
 * the OTP is invalidated and must be regenerated.</p>
 */
@Entity
@Table(
        name = "mobile_otps",
        indexes = {
                @Index(name = "idx_otp_user_id",      columnList = "user_id"),
                @Index(name = "idx_otp_phone_number", columnList = "phone_number"),
                @Index(name = "idx_otp_used",         columnList = "used")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileOtp extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** BCrypt hash of the 6-digit OTP — never store the raw OTP. */
    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    /** Phone number this OTP was sent to. */
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    /** When this OTP expires (typically 10 minutes after generation). */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /** {@code true} once successfully verified. */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = Boolean.FALSE;

    /** Number of failed verification attempts for this OTP. */
    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    /**
     * Returns {@code true} if the OTP is still verifiable.
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && LocalDateTime.now().isBefore(expiryDate);
    }
}
