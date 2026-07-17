package com.courtai.notification.service;

import com.courtai.notification.event.BusinessNotificationEvent;

public interface NotificationEventService {
    void handleBusinessEvent(BusinessNotificationEvent event);
    void retryFailedEvents();
}