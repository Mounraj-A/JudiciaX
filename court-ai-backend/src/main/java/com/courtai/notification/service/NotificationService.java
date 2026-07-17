package com.courtai.notification.service;

/**
 * Service contract for sending and managing user notifications.
 *
 * <p>Full implementation (email, SMS, push, in-app) will be completed in Phase 2.</p>
 */
public interface NotificationService {

    /**
     * Sends an in-app notification to a user.
     *
     * @param recipientUuid UUID of the recipient user
     * @param title         notification title
     * @param message       notification message body
     * @param referenceUuid UUID of the related entity (e.g. case UUID)
     * @param referenceType type of the related entity (e.g. "CaseFile")
     */
    void sendInAppNotification(String recipientUuid, String title, String message,
                               String referenceUuid, String referenceType);

    /**
     * Returns the count of unread notifications for a user.
     *
     * @param userUuid the user's UUID
     * @return count of unread notifications
     */
    long getUnreadCount(String userUuid);

    void distributeNotification(String recipientUuid, String templateCode, java.util.Map<String, Object> variables,
                                String referenceUuid, String referenceType);

    void markAllAsRead(String userUuid);

    void deleteNotification(String uuid);
}
