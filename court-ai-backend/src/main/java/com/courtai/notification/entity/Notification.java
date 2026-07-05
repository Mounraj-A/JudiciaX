package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.NotificationType;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Notification entity for in-system and out-of-system alerts.
 *
 * <p>Supports multiple delivery channels: IN_APP, EMAIL, SMS.</p>
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_recipient", columnList = "recipient_id"),
                @Index(name = "idx_notification_is_read", columnList = "is_read"),
                @Index(name = "idx_notification_type", columnList = "notification_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    private NotificationType notificationType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_uuid", length = 36)
    private String referenceUuid;  // UUID of the related entity (e.g., case UUID)

    @Column(name = "reference_type", length = 100)
    private String referenceType;  // e.g., CaseFile, Hearing

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = Boolean.FALSE;

    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = Boolean.FALSE;
}
