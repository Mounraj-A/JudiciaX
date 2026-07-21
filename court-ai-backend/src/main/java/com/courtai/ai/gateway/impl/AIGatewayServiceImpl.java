package com.courtai.ai.gateway.impl;

import com.courtai.ai.client.AIClient;
import com.courtai.ai.config.AIProperties;
import com.courtai.ai.dto.AIRequestContext;
import com.courtai.ai.dto.AIResponseMetadata;
import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.dto.GatewayResponse;
import com.courtai.ai.exception.AIUnavailableException;
import com.courtai.ai.gateway.AIGatewayService;
import com.courtai.ai.monitor.GatewayMetrics;
import com.courtai.ai.registry.AIServiceRegistry;
import com.courtai.ai.registry.RegisteredService;
import com.courtai.ai.util.IdGeneratorUtil;
import com.courtai.ai.util.SecurityContextUtil;
import com.courtai.ai.validator.RequestValidator;
import com.courtai.ai.validator.ResponseValidator;
import com.courtai.audit.service.AuditService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIGatewayServiceImpl implements AIGatewayService {

    private final AIProperties properties;
    private final AIServiceRegistry registry;
    private final AIClient client;
    private final RequestValidator requestValidator;
    private final ResponseValidator responseValidator;
    private final GatewayMetrics metrics;
    private final AuditService auditService; // Existing audit service

    private static final String RESILIENCE_INSTANCE = "aiGateway";

    @Override
    @Retry(name = RESILIENCE_INSTANCE, fallbackMethod = "fallbackProcessRequest")
    @CircuitBreaker(name = RESILIENCE_INSTANCE)
    @RateLimiter(name = RESILIENCE_INSTANCE)
    @Bulkhead(name = RESILIENCE_INSTANCE, type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public <T, R> CompletableFuture<GatewayResponse<R>> processRequest(GatewayRequest<T> request, Class<R> responseType) {
        long startTime = System.currentTimeMillis();

        // 1. Context Enrichment
        enrichContext(request);

        // 2. Validation
        requestValidator.validate(request);

        // 3. Service Resolution
        RegisteredService targetService = registry.getService(request.getTargetService())
                .orElseThrow(() -> new AIUnavailableException("AI Service not registered: " + request.getTargetService()));

        if (!targetService.isEnabled()) {
            throw new AIUnavailableException("AI Service is currently disabled: " + request.getTargetService());
        }

        // 4. Audit Log (Request)
        auditService.logSuccess(
                "AI_REQUEST",
                "AI_SERVICE",
                targetService.getServiceId(),
                "Dispatching request " + request.getContext().getRequestId() + " to " + targetService.getName()
        );

        // 5. Send Request
        return client.sendRequest(targetService, request, responseType)
                .thenApply(responsePayload -> {
                    long latency = System.currentTimeMillis() - startTime;
                    metrics.recordSuccess(latency);

                    GatewayResponse<R> gatewayResponse = GatewayResponse.<R>builder()
                            .metadata(AIResponseMetadata.builder()
                                    .correlationId(request.getContext().getCorrelationId())
                                    .requestId(request.getContext().getRequestId())
                                    .gatewayVersion(properties.getApiVersion())
                                    .aiModelVersion(targetService.getVersion())
                                    .apiVersion(properties.getApiVersion())
                                    .processingTimeMs(latency)
                                    .generatedTimestamp(LocalDateTime.now())
                                    .status("SUCCESS")
                                    .build())
                            .payload(responsePayload)
                            .build();

                    // Validate Response
                    responseValidator.validate(gatewayResponse, request.getContext().getCorrelationId());

                    // Audit Log (Response)
                    auditService.logSuccess(
                            "AI_SUCCESS",
                            "AI_SERVICE",
                            targetService.getServiceId(),
                            "Received successful response for " + request.getContext().getRequestId()
                    );

                    return gatewayResponse;
                })
                .exceptionally(ex -> {
                    long latency = System.currentTimeMillis() - startTime;
                    metrics.recordFailure(latency);
                    auditService.logFailure(
                            "AI_FAILURE",
                            "AI_SERVICE",
                            targetService.getServiceId(),
                            "Failed request " + request.getContext().getRequestId() + ". Error: " + ex.getMessage()
                    );
                    throw new RuntimeException("AI Gateway processing failed", ex);
                });
    }

    /**
     * Fallback method triggered by Resilience4j when retries are exhausted or circuit is open.
     */
    public <T, R> CompletableFuture<GatewayResponse<R>> fallbackProcessRequest(GatewayRequest<T> request, Class<R> responseType, Throwable t) {
        log.error("Fallback triggered for request {}: {}", request.getContext().getRequestId(), t.getMessage());
        metrics.recordFailure(0);

        auditService.logFailure(
                "AI_FALLBACK",
                "AI_SERVICE",
                request.getTargetService(),
                "Fallback activated for request " + request.getContext().getRequestId() + " due to: " + t.getMessage()
        );

        // Throwing an exception to ensure it doesn't silently return null or an empty response.
        CompletableFuture<GatewayResponse<R>> future = new CompletableFuture<>();
        future.completeExceptionally(new AIUnavailableException("AI Service is unavailable and fallback was triggered", t));
        return future;
    }

    private <T> void enrichContext(GatewayRequest<T> request) {
        if (request.getContext() == null) {
            request.setContext(new AIRequestContext());
        }
        AIRequestContext ctx = request.getContext();
        
        if (ctx.getRequestId() == null || ctx.getRequestId().isBlank()) {
            ctx.setRequestId(IdGeneratorUtil.generateRequestId());
        }
        if (ctx.getCorrelationId() == null || ctx.getCorrelationId().isBlank()) {
            ctx.setCorrelationId(IdGeneratorUtil.generateCorrelationId());
        }
        
        ctx.setUserUuid(SecurityContextUtil.getCurrentUserUuid());
        ctx.setUserRole(SecurityContextUtil.getCurrentUserRole());
        ctx.setTimestamp(LocalDateTime.now());
        ctx.setApiVersion(properties.getApiVersion());
    }
}
