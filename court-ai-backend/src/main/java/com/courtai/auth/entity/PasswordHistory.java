package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores BCrypt hashes of previously used passwords for password history enforcement.
 *
 * <p>Before allowing a password change, the last
 * {@link com.courtai.common.constants.AppConstants#PASSWORD_HISTORY_COUNT} entries
 * for this user are checked. The new password must not match any of them.</p>
 */
@Entity
@Table(
        name = "password_histories",
        indexes = {
                @Index(name = "idx_ph_user_id",    columnList = "user_id"),
                @Index(name = "idx_ph_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** BCrypt hash of the previously used password. */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** When this password was set — used to maintain rolling history. */
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
