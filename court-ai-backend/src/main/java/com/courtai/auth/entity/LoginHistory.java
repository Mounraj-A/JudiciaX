package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.LoginStatus;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records every login attempt — successful or failed.
 *
 * <p>Used for security monitoring, suspicious activity detection,
 * and the admin authentication dashboard.</p>
 */
@Entity
@Table(
        name = "login_history",
        indexes = {
                @Index(name = "idx_lh_user_id",    columnList = "user_id"),
                @Index(name = "idx_lh_login_time", columnList = "login_time"),
                @Index(name = "idx_lh_status",     columnList = "status"),
                @Index(name = "idx_lh_ip_address", columnList = "ip_address")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** When the login attempt occurred. */
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    /** When the user logged out (null if still active). */
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    /** Client IP address. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Parsed device type (e.g., "Desktop", "Mobile"). */
    @Column(name = "device", length = 100)
    private String device;

    /** Parsed browser (e.g., "Chrome 125"). */
    @Column(name = "browser", length = 100)
    private String browser;

    /** Parsed operating system (e.g., "Windows 11"). */
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    /** Whether this device/IP was previously seen for this user. */
    @Column(name = "is_trusted_device", nullable = false)
    @Builder.Default
    private Boolean isTrustedDevice = Boolean.FALSE;

    /** Outcome of this login attempt. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoginStatus status;

    /** UUID of the {@link UserSession} created on successful login. */
    @Column(name = "session_id", length = 36)
    private String sessionId;
}
