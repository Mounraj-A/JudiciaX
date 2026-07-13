package com.courtai.admin.service;

import java.util.Map;

/** Admin service for AI system configuration and usage monitoring. */
public interface AIAdministrationService {
    Map<String, Object> getAiSettings();
    void enableAi(String adminUuid);
    void disableAi(String adminUuid);
    void updateModelVersion(String version, String adminUuid);
    void updatePriorityThreshold(int threshold, String adminUuid);
    void updateConfidenceThreshold(int threshold, String adminUuid);
    void setExplainabilityEnabled(boolean enabled, String adminUuid);
    Map<String, Object> getAiUsageStats();
}
