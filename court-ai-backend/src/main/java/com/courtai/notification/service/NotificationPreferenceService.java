package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;

import java.util.List;

public interface NotificationPreferenceService {
    NotificationPreferenceResponse updatePreference(String userUuid, NotificationPreferenceRequest request);
    List<NotificationPreferenceResponse> getUserPreferences(String userUuid);
    boolean isDeliveryEnabled(String userUuid, com.courtai.common.enums.NotificationType channel, String notificationType);
}