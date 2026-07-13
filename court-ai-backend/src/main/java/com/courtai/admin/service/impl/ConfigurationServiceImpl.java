package com.courtai.admin.service.impl;

import com.courtai.admin.dto.ConfigurationRequest;
import com.courtai.admin.dto.ConfigurationResponse;
import com.courtai.admin.entity.SystemConfiguration;
import com.courtai.admin.mapper.ConfigurationMapper;
import com.courtai.admin.repository.SystemConfigurationRepository;
import com.courtai.admin.service.ConfigurationService;
import com.courtai.audit.service.AuditService;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationServiceImpl implements ConfigurationService {

    private final SystemConfigurationRepository repo;
    private final ConfigurationMapper           mapper;
    private final AuditService                  auditService;

    @Override
    public Page<ConfigurationResponse> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public ConfigurationResponse getByKey(String key) {
        return mapper.toResponse(repo.findByConfigKeyAndIsDeletedFalse(key)
                .orElseThrow(() -> new ResourceNotFoundException("SystemConfiguration", "key", key)));
    }

    @Override
    public ConfigurationResponse getByUuid(String uuid) {
        return mapper.toResponse(load(uuid));
    }

    @Override
    @Transactional
    public ConfigurationResponse create(ConfigurationRequest req, String adminUuid) {
        if (repo.existsByConfigKey(req.getConfigKey())) {
            throw new BusinessRuleViolationException(
                    "Config key '" + req.getConfigKey() + "' already exists.");
        }
        SystemConfiguration config = SystemConfiguration.builder()
                .configKey(req.getConfigKey())
                .configValue(req.getConfigValue())
                .description(req.getDescription())
                .category(req.getCategory() != null ? req.getCategory() : "GENERAL")
                .isEditable(req.getIsEditable() != null ? req.getIsEditable() : Boolean.TRUE)
                .updatedByAdmin(adminUuid)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
        repo.save(config);
        auditService.logSuccess("CONFIG_CREATED", "SystemConfiguration", config.getUuid(),
                "Key '" + req.getConfigKey() + "' created by admin " + adminUuid);
        return mapper.toResponse(config);
    }

    @Override
    @Transactional
    public ConfigurationResponse update(String uuid, ConfigurationRequest req, String adminUuid) {
        SystemConfiguration config = load(uuid);
        if (Boolean.FALSE.equals(config.getIsEditable())) {
            throw new BusinessRuleViolationException(
                    "Configuration key '" + config.getConfigKey() + "' is read-only and cannot be modified.");
        }
        if (req.getConfigValue()  != null) config.setConfigValue(req.getConfigValue());
        if (req.getDescription()  != null) config.setDescription(req.getDescription());
        if (req.getCategory()     != null) config.setCategory(req.getCategory());
        config.setUpdatedByAdmin(adminUuid);
        config.setLastUpdatedAt(LocalDateTime.now());
        repo.save(config);
        auditService.logSuccess("CONFIG_UPDATED", "SystemConfiguration", uuid,
                "Key '" + config.getConfigKey() + "' updated by admin " + adminUuid);
        return mapper.toResponse(config);
    }

    @Override
    @Transactional
    public void delete(String uuid, String adminUuid) {
        SystemConfiguration config = load(uuid);
        if (Boolean.FALSE.equals(config.getIsEditable())) {
            throw new BusinessRuleViolationException("Cannot delete a read-only configuration.");
        }
        config.softDelete();
        repo.save(config);
        auditService.logSuccess("CONFIG_DELETED", "SystemConfiguration", uuid,
                "Deleted by admin " + adminUuid);
    }

    @Override
    public String getValue(String key) {
        return repo.findByConfigKeyAndIsDeletedFalse(key)
                .map(SystemConfiguration::getConfigValue).orElse(null);
    }

    private SystemConfiguration load(String uuid) {
        return repo.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SystemConfiguration", "uuid", uuid));
    }
}
