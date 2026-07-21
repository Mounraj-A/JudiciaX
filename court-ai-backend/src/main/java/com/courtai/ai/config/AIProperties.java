package com.courtai.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Enterprise configuration properties for the AI Gateway.
 * Mapped to 'ai' prefix in application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIProperties {

    private boolean enabled;
    private String apiVersion;
    private String secretKey;
    private int connectTimeout;
    private int readTimeout;
    private int requestTimeout;
    private int maxRetry;
    private long retryDelay;
    private String healthEndpoint;

    private AIFeatures features;
    private AIRegistry registry;

    @Data
    public static class AIFeatures {
        private boolean ocrEnabled;
        private boolean nlpEnabled;
        private boolean priorityEngineEnabled;
        private boolean ctsEnabled;
        private boolean explainabilityEnabled;
        private boolean recommendationEnabled;
        private boolean simulationMode;
        private boolean maintenanceMode;
        private boolean readOnlyMode;
    }

    @Data
    public static class AIRegistry {
        private Map<String, ServiceConfig> services;
    }

    @Data
    public static class ServiceConfig {
        private String name;
        private String version;
        private String endpoint;
        private boolean enabled;
        private int timeout;
        private String status;
    }
}
