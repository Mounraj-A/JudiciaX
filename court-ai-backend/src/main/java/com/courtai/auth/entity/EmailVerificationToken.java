package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Email verification token — sent to users after registration to confirm their email.
 *
 * <p>Only the SHA-256 hash of the UUID token is stored.
 * The raw token is returned in the API response (simulating email delivery).
 * Expiry is configured via {@link com.courtai.common.constants.AppConstants#EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS}.</p>
 */
@Entity
@Table(
        name = "email_verification_tokens",
        indexes = {
                @Index(name = "idx_evt_token_hash", columnList = "token_hash"),
                @Index(name = "idx_evt_user_id",    columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** SHA-256 hash of the raw UUID token. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** Token expiry — typically 24 hours after creation. */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /** {@code true} once the email has been verified. */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = Boolean.FALSE;

    /**
     * Returns {@code true} if the token is still valid.
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && LocalDateTime.now().isBefore(expiryDate);
    }
}
