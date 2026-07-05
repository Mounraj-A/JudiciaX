package com.courtai.user.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Core user entity representing any authenticated system participant.
 *
 * <p>All role-specific profiles (Judge, Clerk, Advocate, Admin) will have
 * a one-to-one relationship with this entity.</p>
 *
 * <p>Password is stored as a BCrypt hash — never stored in plaintext.</p>
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_role", columnList = "role"),
                @Index(name = "idx_users_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * Unique username used for login and display.
     */
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    /**
     * Unique email address — used as the primary login identifier.
     */
    @Email
    @NotBlank
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /**
     * BCrypt-hashed password. Never store or expose in plaintext.
     */
    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * User's first name.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Contact phone number (optional).
     */
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Role assigned to this user — determines access rights.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    /**
     * Whether the account is active and can be used for login.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;

    /**
     * Whether the account's email address has been verified.
     */
    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = Boolean.FALSE;

    /**
     * Whether the account has been locked (e.g., due to multiple failed logins).
     */
    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = Boolean.FALSE;

    /**
     * Convenience method: returns the full name of the user.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
