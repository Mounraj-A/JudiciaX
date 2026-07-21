package com.courtai.ai.client;

import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.registry.RegisteredService;

import java.util.concurrent.CompletableFuture;

/**
 * Generic AI Client interface that strictly delegates communication to external services.
 * Contains no business logic.
 */
public interface AIClient {

    /**
     * Checks if the specific AI service is reachable.
     */
    boolean validateConnection(RegisteredService service);

    /**
     * Sends a generic payload to the registered service.
     */
    <T, R> CompletableFuture<R> sendRequest(RegisteredService service, GatewayRequest<T> request, Class<R> responseType);
}
