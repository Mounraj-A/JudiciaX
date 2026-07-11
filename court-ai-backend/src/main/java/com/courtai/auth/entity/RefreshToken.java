package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persisted refresh token — enables secure token rotation.
 *
 * <p>The raw token value is NEVER stored. Only its SHA-256 hash is persisted
 * so that a database breach cannot expose valid refresh tokens.</p>
 *
 * <p>On each {@code POST /auth/refresh}:</p>
 * <ol>
 *   <li>Verify token hash matches</li>
 *   <li>Check not {@code used} and not expired</li>
 *   <li>Mark {@code used = true}</li>
 *   <li>Issue new access + refresh token pair</li>
 * </ol>
 */
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_used", columnList = "used")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** SHA-256 hash of the raw refresh token — never the token itself. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** When this token expires. */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /** {@code true} once this token has been exchanged for a new pair (rotation). */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = Boolean.FALSE;

    /** Links this token to a {@link UserSession} for device-level tracking. */
    @Column(name = "session_id", length = 36)
    private String sessionId;

    /** IP address from which this refresh was requested. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Returns {@code true} if the token is still usable (not used, not expired).
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && LocalDateTime.now().isBefore(expiryDate);
    }
}
