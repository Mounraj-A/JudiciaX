package com.courtai.auth.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records security-relevant events for the admin security monitoring dashboard.
 *
 * <p>Written asynchronously via {@link com.courtai.auth.service.SecurityEventService}
 * to avoid impacting request latency. Never propagates write failures to the caller.</p>
 */
@Entity
@Table(
        name = "security_events",
        indexes = {
                @Index(name = "idx_se_user_id",    columnList = "user_id"),
                @Index(name = "idx_se_event_type", columnList = "event_type"),
                @Index(name = "idx_se_created_at", columnList = "event_time"),
                @Index(name = "idx_se_ip_address", columnList = "ip_address"),
                @Index(name = "idx_se_severity",   columnList = "severity")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityEvent extends BaseEntity {

    /**
     * The user involved in this event — nullable for unauthenticated events
     * (e.g., failed login against non-existent email).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** Classification of the security event. */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 60)
    private SecurityEventType eventType;

    /** Human-readable description of the event. */
    @Column(name = "description", length = 1000)
    private String description;

    /** IP address of the request that triggered this event. */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Parsed device string. */
    @Column(name = "device", length = 100)
    private String device;

    /** Parsed browser string. */
    @Column(name = "browser", length = 100)
    private String browser;

    /** Severity level: LOW, MEDIUM, HIGH, CRITICAL. */
    @Column(name = "severity", length = 20, nullable = false)
    @Builder.Default
    private String severity = "LOW";

    /** Timestamp the event occurred. */
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    /** Email of the user involved (denormalised for quick lookup). */
    @Column(name = "actor_email", length = 150)
    private String actorEmail;
}
