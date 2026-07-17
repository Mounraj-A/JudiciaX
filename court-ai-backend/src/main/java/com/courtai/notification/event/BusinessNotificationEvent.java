package com.courtai.notification.event;

import com.courtai.common.enums.SystemNotificationEvent;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class BusinessNotificationEvent {
    private final SystemNotificationEvent eventType;
    private final String referenceUuid;
    private final Enum<?> module;
    private final Map<String, Object> payload;
    private final Set<String> targetUserUuids; // Optional, overrides resolution if set
}