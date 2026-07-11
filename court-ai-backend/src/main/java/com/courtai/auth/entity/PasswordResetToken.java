package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Password reset token — one-time token for secure password recovery.
 *
 * <p>Only the SHA-256 hash of the token is stored. The raw UUID token
 * is returned to the caller (simulating email delivery in this phase).</p>
 *
 * <p>A token is valid for {@link com.courtai.common.constants.AppConstants#PASSWORD_RESET_TOKEN_EXPIRY_HOURS} hours.</p>
 */
@Entity
@Table(
        name = "password_reset_tokens",
        indexes = {
                @Index(name = "idx_prt_token_hash", columnList = "token_hash"),
                @Index(name = "idx_prt_user_id",    columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** SHA-256 hash of the raw reset token. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** Token expiry timestamp. */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /** {@code true} once the token has been successfully used. */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = Boolean.FALSE;

    /** IP address of the requester. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Returns {@code true} if the token is still valid (not used, not expired).
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && LocalDateTime.now().isBefore(expiryDate);
    }
}
