package com.courtai.ai.gateway;

import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.dto.GatewayResponse;

import java.util.concurrent.CompletableFuture;

/**
 * The Enterprise AI Orchestration Gateway.
 * The ONLY entry point for Spring Boot to communicate with external FastAPI AI models.
 */
public interface AIGatewayService {

    /**
     * Dispatches a request to the designated AI service asynchronously.
     * Applies Resilience4j strategies (Retry, Circuit Breaker, etc.).
     *
     * @param request The generic AI Request with context.
     * @param responseType The expected type of the inner payload.
     * @return CompletableFuture of the GatewayResponse.
     */
    <T, R> CompletableFuture<GatewayResponse<R>> processRequest(GatewayRequest<T> request, Class<R> responseType);
}
