package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.SessionStatus;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks active and historical user sessions across devices.
 *
 * <p>Enables: logout from all devices, suspicious login detection,
 * active session listing, and per-session revocation.</p>
 *
 * <p>Access and refresh token hashes are stored here (SHA-256),
 * never the raw token values.</p>
 */
@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_session_user_id",      columnList = "user_id"),
                @Index(name = "idx_session_status",       columnList = "status"),
                @Index(name = "idx_session_access_hash",  columnList = "access_token_hash"),
                @Index(name = "idx_session_refresh_hash", columnList = "refresh_token_hash"),
                @Index(name = "idx_session_ip_address",   columnList = "ip_address")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** SHA-256 hash of the current access token. */
    @Column(name = "access_token_hash", length = 64)
    private String accessTokenHash;

    /** SHA-256 hash of the current refresh token. */
    @Column(name = "refresh_token_hash", length = 64)
    private String refreshTokenHash;

    /** Client IP address at time of login. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Parsed browser string (e.g., "Chrome 125"). */
    @Column(name = "browser", length = 100)
    private String browser;

    /** Parsed device type (e.g., "Desktop"). */
    @Column(name = "device", length = 100)
    private String device;

    /** Parsed operating system (e.g., "Windows 11"). */
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    /** Session start time. */
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    /** Session end time — null while active. */
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    /** Last API call time for idle detection. */
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    /** Whether this device was previously seen for this user. */
    @Column(name = "is_trusted_device", nullable = false)
    @Builder.Default
    private Boolean isTrustedDevice = Boolean.FALSE;

    /** Current lifecycle status of this session. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;
}
