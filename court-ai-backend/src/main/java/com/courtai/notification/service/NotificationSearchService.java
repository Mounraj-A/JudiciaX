package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import org.springframework.data.domain.Page;

public interface NotificationSearchService {
    Page<NotificationResponse> search(String userUuid, NotificationSearchRequest request);
}