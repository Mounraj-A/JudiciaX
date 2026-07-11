package com.courtai.common.enums;

/**
 * Delivery status of a notification sent to a recipient.
 *
 * <p>Used by {@link com.courtai.notification.entity.NotificationRecipient}
 * to track per-channel delivery lifecycle.</p>
 */
public enum DeliveryStatus {

    /** Notification dispatched to the delivery channel. */
    SENT,

    /** Confirmed delivered to the recipient's device/inbox. */
    DELIVERED,

    /** Recipient has opened/read the notification. */
    READ,

    /** Delivery attempt failed — see failureReason for details. */
    FAILED
}
