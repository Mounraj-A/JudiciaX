package com.courtai.notification.service.impl;

import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.entity.NotificationTemplate;
import com.courtai.notification.mapper.NotificationTemplateMapper;
import com.courtai.notification.repository.NotificationTemplateRepository;
import com.courtai.notification.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository repository;
    private final NotificationTemplateMapper mapper;

    @Override
    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        if (repository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new IllegalArgumentException("Template code already exists");
        }
        NotificationTemplate template = mapper.toEntity(request);
        template.setUuid(UUID.randomUUID().toString());
        return mapper.toResponse(repository.save(template));
    }

    @Override
    @Transactional
    public NotificationTemplateResponse updateTemplate(String uuid, NotificationTemplateRequest request) {
        NotificationTemplate template = getEntityByUuid(uuid);
        if (!template.getCode().equals(request.getCode()) && repository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new IllegalArgumentException("Template code already exists");
        }
        mapper.updateEntityFromRequest(request, template);
        return mapper.toResponse(repository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getTemplateByUuid(String uuid) {
        return mapper.toResponse(getEntityByUuid(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getAllTemplates() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    @Transactional
    public void deleteTemplate(String uuid) {
        NotificationTemplate template = getEntityByUuid(uuid);
        template.setIsDeleted(true);
        repository.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    public String render(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = repository.findByCodeAndIsDeletedFalse(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "code", templateCode));
        return renderText(template.getBody(), variables);
    }

    @Override
    @Transactional(readOnly = true)
    public String renderSubject(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = repository.findByCodeAndIsDeletedFalse(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "code", templateCode));
        return renderText(template.getSubject(), variables);
    }

    private String renderText(String text, Map<String, Object> variables) {
        if (text == null) return "";
        if (variables == null || variables.isEmpty()) return text;
        String result = text;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    private NotificationTemplate getEntityByUuid(String uuid) {
        return repository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "uuid", uuid));
    }
}