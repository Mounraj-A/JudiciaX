package com.courtai.notification.entity;

import com.courtai.common.entity.BaseEntity;
import com.courtai.common.enums.DeliveryStatus;
import com.courtai.common.enums.NotificationType;
import com.courtai.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDelivery extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "read_at")
    private ZonedDateTime readAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "read_ip")
    private String readIp;

    @Column(name = "read_device")
    private String readDevice;
}