package com.courtai.user.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Core user entity representing any authenticated system participant.
 *
 * <p>All role-specific profiles (Judge, Clerk, Advocate, Admin) have
 * a one-to-one relationship with this entity.</p>
 *
 * <p>Password is stored as a BCrypt hash — never stored in plaintext.</p>
 *
 * <p>Uses {@code accountStatus} for lifecycle management and
 * {@code accountLockedUntil} for timed brute-force lockouts.</p>
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email",    columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_phone",    columnNames = "phone_number")
        },
        indexes = {
                @Index(name = "idx_users_email",          columnList = "email"),
                @Index(name = "idx_users_role",           columnList = "role"),
                @Index(name = "idx_users_is_deleted",     columnList = "is_deleted"),
                @Index(name = "idx_users_account_status", columnList = "account_status"),
                @Index(name = "idx_users_username",       columnList = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    // =========================================================
    //  IDENTITY (existing V1 columns)
    // =========================================================

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    /** Legacy first name column — kept for V1 compatibility. */
    @Size(max = 100)
    @Column(name = "first_name", length = 100)
    private String firstName;

    /** Legacy last name column — kept for V1 compatibility. */
    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    /** Full display name added in V5 migration. */
    @Size(min = 3, max = 200)
    @Column(name = "full_name", length = 200)
    private String fullName;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Pattern(regexp = "^$|^(\\+91-)?[6-9]\\d{9}$", message = "Phone number must be 10 digits starting with 6-9 (optional +91-), or empty")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // =========================================================
    //  ROLE & STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    /**
     * Fine-grained lifecycle status — added in V5 migration.
     * New code uses this; old code uses {@code isActive}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 30)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

    // =========================================================
    //  VERIFICATION FLAGS (existing + new)
    // =========================================================

    /** Existing V1 column. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    /** Existing V1 column. */
    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = Boolean.FALSE;

    /** Existing V1 column — legacy lock flag. New code uses accountStatus=LOCKED. */
    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = Boolean.FALSE;

    /** Added in V5 migration — mobile OTP verified flag. */
    @Column(name = "is_mobile_verified")
    @Builder.Default
    private Boolean isMobileVerified = Boolean.FALSE;

    // =========================================================
    //  BRUTE FORCE PROTECTION (added in V5 migration)
    // =========================================================

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    // =========================================================
    //  ACTIVITY TRACKING (added in V5 migration)
    // =========================================================

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_logout")
    private LocalDateTime lastLogout;

    // =========================================================
    //  PROFILE COMPLETENESS (added in V5 migration)
    // =========================================================

    @Column(name = "profile_completion_percent")
    @Builder.Default
    private Integer profileCompletionPercent = 0;

    // =========================================================
    //  CONVENIENCE METHODS
    // =========================================================

    /**
     * Returns the full name, or falls back to firstName+lastName, then username.
     */
    public String getDisplayName() {
        if (fullName != null && !fullName.isBlank()) return fullName;
        if (firstName != null && lastName != null) return firstName + " " + lastName;
        return username;
    }

    /**
     * Returns {@code true} if the timed lock has expired (account should auto-unlock).
     */
    public boolean isTimedLockExpired() {
        return accountLockedUntil != null && LocalDateTime.now().isAfter(accountLockedUntil);
    }

    /**
     * Returns {@code true} if the account is effectively locked right now.
     */
    public boolean isEffectivelyLocked() {
        if (accountStatus == AccountStatus.LOCKED) return !isTimedLockExpired();
        return Boolean.TRUE.equals(isLocked);
    }
}
