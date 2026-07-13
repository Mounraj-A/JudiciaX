package com.courtai.admin.service;

import com.courtai.admin.dto.ConfigurationRequest;
import com.courtai.admin.dto.ConfigurationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** System configuration management service. */
public interface ConfigurationService {
    Page<ConfigurationResponse> getAll(Pageable pageable);
    ConfigurationResponse getByKey(String configKey);
    ConfigurationResponse getByUuid(String uuid);
    ConfigurationResponse create(ConfigurationRequest request, String adminUuid);
    ConfigurationResponse update(String uuid, ConfigurationRequest request, String adminUuid);
    void delete(String uuid, String adminUuid);
    String getValue(String key);
}
