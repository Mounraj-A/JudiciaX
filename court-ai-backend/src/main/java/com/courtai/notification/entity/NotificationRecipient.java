package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.DeliveryStatus;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks per-user, per-channel delivery status of a {@link Notification}.
 *
 * <p>Enables broadcast notifications (one notification sent to multiple users)
 * and granular per-channel delivery tracking (IN_APP, EMAIL, SMS, PUSH).</p>
 *
 * <p>A unique constraint on (notification_id, user_id, delivery_channel)
 * prevents duplicate delivery records for the same channel.</p>
 */
@Entity
@Table(
        name = "notification_recipients",
        indexes = {
                @Index(name = "idx_nrecip_notification_id", columnList = "notification_id"),
                @Index(name = "idx_nrecip_user_id",         columnList = "user_id"),
                @Index(name = "idx_nrecip_status",          columnList = "delivery_status"),
                @Index(name = "idx_nrecip_is_deleted",      columnList = "is_deleted")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_nrecip_notification_user_channel",
                        columnNames = {"notification_id", "user_id", "delivery_channel"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRecipient extends BaseEntity {

    /** The source notification. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /** The user receiving this notification. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Delivery channel for this recipient record.
     * Values: IN_APP, EMAIL, SMS, PUSH
     */
    @Column(name = "delivery_channel", nullable = false, length = 20)
    @Builder.Default
    private String deliveryChannel = "IN_APP";

    /** Current delivery status for this channel. */
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    /** Timestamp when the notification was dispatched to this channel. */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /** Timestamp when delivery was confirmed by the channel provider. */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    /** Timestamp when the user opened or read the notification. */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /** Failure reason if delivery could not be completed. */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
}
