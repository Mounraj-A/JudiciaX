package com.courtai.admin.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records security-significant login events for admin monitoring.
 *
 * <p>Supplements the existing {@link com.courtai.auth.entity.LoginHistory}
 * with richer device/browser/IP context for security audit purposes.
 * Managed exclusively by the admin security monitoring dashboard.</p>
 */
@Entity
@Table(
        name = "login_security_events",
        indexes = {
                @Index(name = "idx_lse_user_id",    columnList = "user_id"),
                @Index(name = "idx_lse_event_type", columnList = "event_type"),
                @Index(name = "idx_lse_status",     columnList = "status"),
                @Index(name = "idx_lse_created_at", columnList = "created_at"),
                @Index(name = "idx_lse_ip_address", columnList = "ip_address")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginSecurityEvent extends BaseEntity {

    /**
     * Type of security event.
     * Values: FAILED_LOGIN, ACCOUNT_LOCKED, SUSPICIOUS_LOGIN,
     *         CONCURRENT_SESSION, JWT_REVOKED, OTP_FAILURE, BRUTE_FORCE
     */
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "browser", length = 200)
    private String browser;

    @Column(name = "device", length = 200)
    private String device;

    /**
     * Outcome of the event.
     * Values: BLOCKED, WARNED, LOGGED
     */
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "LOGGED";

    @Column(name = "details", length = 1000)
    private String details;

    /** Explicit timestamp override (BaseEntity.createdAt is used by default). */
    @Column(name = "event_time")
    private LocalDateTime eventTime;
}
