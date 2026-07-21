package com.courtai.ai.health;

import com.courtai.ai.monitor.GatewayMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Health service specifically for the AI Infrastructure Layer.
 */
@Service
@RequiredArgsConstructor
public class AIHealthService {

    private final GatewayMetrics metrics;

    public GatewayStatus getGatewayStatus() {
        return metrics.getCurrentStatus().get();
    }

    public void setGatewayStatus(GatewayStatus status) {
        metrics.getCurrentStatus().set(status);
    }

    public Map<String, Object> checkGateway() {
        return Map.of(
            "status", getGatewayStatus().name(),
            "uptime_ms", metrics.getUptimeMs(),
            "metrics", Map.of(
                "total_requests", metrics.getTotalRequests().get(),
                "success", metrics.getSuccessfulRequests().get(),
                "failures", metrics.getFailedRequests().get(),
                "retries", metrics.getRetryCount().get(),
                "timeouts", metrics.getTimeoutCount().get(),
                "avg_latency", metrics.getAverageLatency()
            )
        );
    }
    
    public Map<String, String> ping() {
        return Map.of("message", "pong");
    }
    
    public Map<String, String> version() {
        return Map.of("gateway_version", "v1.0", "api_version", "v1.0");
    }
}
