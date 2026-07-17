package com.courtai.notification.service;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;

import java.util.List;
import java.util.Map;

public interface NotificationTemplateService {
    NotificationTemplateResponse createTemplate(NotificationTemplateRequest request);
    NotificationTemplateResponse updateTemplate(String uuid, NotificationTemplateRequest request);
    NotificationTemplateResponse getTemplateByUuid(String uuid);
    List<NotificationTemplateResponse> getAllTemplates();
    void deleteTemplate(String uuid);
    
    /**
     * Renders a template body by replacing placeholders with variables.
     */
    String render(String templateCode, Map<String, Object> variables);
    String renderSubject(String templateCode, Map<String, Object> variables);
}