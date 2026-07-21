package com.courtai.ai.controller;

import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.dto.GatewayResponse;
import com.courtai.ai.gateway.AIGatewayService;
import com.courtai.ai.health.AIHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise AI Gateway Controller.
 * Exposes infrastructure and testing endpoints. Business integration will happen via service-to-service calls.
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI Gateway", description = "Enterprise AI Integration Gateway endpoints")
public class AIGatewayController {

    private final AIHealthService healthService;
    private final AIGatewayService gatewayService;

    @GetMapping("/health")
    @Operation(summary = "Get AI Gateway health status", description = "Returns the status of the gateway and aggregated metrics.")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(healthService.checkGateway());
    }

    @GetMapping("/version")
    @Operation(summary = "Get AI Gateway version", description = "Returns version metadata for the gateway architecture.")
    public ResponseEntity<Map<String, String>> getVersion() {
        return ResponseEntity.ok(healthService.version());
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping AI Gateway", description = "Simple liveness probe for the AI Gateway.")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(healthService.ping());
    }

    @PostMapping("/test")
    @Operation(summary = "Test AI Integration", description = "Dispatches a raw test payload to the configured target service.")
    public CompletableFuture<ResponseEntity<GatewayResponse<Object>>> testAiIntegration(
            @RequestBody GatewayRequest<Object> request) {
        
        return gatewayService.processRequest(request, Object.class)
                .thenApply(ResponseEntity::ok);
    }
}
