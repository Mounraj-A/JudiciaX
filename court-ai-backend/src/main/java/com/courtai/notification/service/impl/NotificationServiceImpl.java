package com.courtai.notification.service.impl;

import com.courtai.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub implementation of {@link NotificationService}.
 *
 * <p><strong>Phase 1 — No-op implementation.</strong>
 * All notification methods log intent but do not deliver messages.
 * Full multi-channel delivery (email, SMS, push, in-app persistence)
 * will be implemented in Phase 2.</p>
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendInAppNotification(String recipientUuid, String title, String message,
                                      String referenceUuid, String referenceType) {
        log.info("[NOTIFICATION-STUB] IN_APP → recipient={} title='{}' ref={}({})",
                recipientUuid, title, referenceType, referenceUuid);
        // Phase 2: Persist to notifications table and push via WebSocket
    }

    @Override
    public long getUnreadCount(String userUuid) {
        log.debug("[NOTIFICATION-STUB] getUnreadCount for user={}", userUuid);
        // Phase 2: Query notification_recipients table for unread count
        return 0L;
    }
}
