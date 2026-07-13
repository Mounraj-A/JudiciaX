package com.courtai.admin.service.impl;

import com.courtai.admin.dto.ConfigurationRequest;
import com.courtai.admin.service.AIAdministrationService;
import com.courtai.admin.service.ConfigurationService;
import com.courtai.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI administration service — delegates all AI settings to
 * {@link ConfigurationService} (key-value pairs in system_configurations).
 */
@Service
@RequiredArgsConstructor
public class AIAdministrationServiceImpl implements AIAdministrationService {

    private static final String KEY_AI_ENABLED          = "AI_ENABLED";
    private static final String KEY_MODEL_VERSION        = "AI_MODEL_VERSION";
    private static final String KEY_PRIORITY_THRESHOLD   = "AI_PRIORITY_THRESHOLD";
    private static final String KEY_CONFIDENCE_THRESHOLD = "AI_CONFIDENCE_THRESHOLD";
    private static final String KEY_EXPLAINABILITY       = "AI_EXPLAINABILITY_ENABLED";

    private final ConfigurationService configService;
    private final AuditService         auditService;

    @Override
    public Map<String, Object> getAiSettings() {
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("aiEnabled",             configService.getValue(KEY_AI_ENABLED));
        settings.put("modelVersion",          configService.getValue(KEY_MODEL_VERSION));
        settings.put("priorityThreshold",     configService.getValue(KEY_PRIORITY_THRESHOLD));
        settings.put("confidenceThreshold",   configService.getValue(KEY_CONFIDENCE_THRESHOLD));
        settings.put("explainabilityEnabled", configService.getValue(KEY_EXPLAINABILITY));
        return settings;
    }

    @Override
    public void enableAi(String adminUuid) {
        updateConfigValue(KEY_AI_ENABLED, "true", adminUuid);
        auditService.logSuccess("AI_ENABLED", "SystemConfiguration", KEY_AI_ENABLED,
                "AI enabled by admin " + adminUuid);
    }

    @Override
    public void disableAi(String adminUuid) {
        updateConfigValue(KEY_AI_ENABLED, "false", adminUuid);
        auditService.logSuccess("AI_DISABLED", "SystemConfiguration", KEY_AI_ENABLED,
                "AI disabled by admin " + adminUuid);
    }

    @Override
    public void updateModelVersion(String version, String adminUuid) {
        updateConfigValue(KEY_MODEL_VERSION, version, adminUuid);
        auditService.logSuccess("AI_MODEL_VERSION_UPDATED", "SystemConfiguration", KEY_MODEL_VERSION,
                "Model version updated to '" + version + "' by admin " + adminUuid);
    }

    @Override
    public void updatePriorityThreshold(int threshold, String adminUuid) {
        updateConfigValue(KEY_PRIORITY_THRESHOLD, String.valueOf(threshold), adminUuid);
        auditService.logSuccess("AI_PRIORITY_THRESHOLD_UPDATED", "SystemConfiguration",
                KEY_PRIORITY_THRESHOLD, "Priority threshold updated to " + threshold);
    }

    @Override
    public void updateConfidenceThreshold(int threshold, String adminUuid) {
        updateConfigValue(KEY_CONFIDENCE_THRESHOLD, String.valueOf(threshold), adminUuid);
        auditService.logSuccess("AI_CONFIDENCE_THRESHOLD_UPDATED", "SystemConfiguration",
                KEY_CONFIDENCE_THRESHOLD, "Confidence threshold updated to " + threshold);
    }

    @Override
    public void setExplainabilityEnabled(boolean enabled, String adminUuid) {
        updateConfigValue(KEY_EXPLAINABILITY, String.valueOf(enabled), adminUuid);
        auditService.logSuccess("AI_EXPLAINABILITY_SET", "SystemConfiguration", KEY_EXPLAINABILITY,
                "Explainability set to " + enabled + " by admin " + adminUuid);
    }

    @Override
    public Map<String, Object> getAiUsageStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("message", "AI usage metrics will be available once the AI microservice is connected.");
        stats.put("aiEnabled", configService.getValue(KEY_AI_ENABLED));
        stats.put("modelVersion", configService.getValue(KEY_MODEL_VERSION));
        return stats;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void updateConfigValue(String key, String value, String adminUuid) {
        String uuid = configService.getByKey(key).getUuid();
        configService.update(uuid, ConfigurationRequest.withValue(value), adminUuid);
    }
}
