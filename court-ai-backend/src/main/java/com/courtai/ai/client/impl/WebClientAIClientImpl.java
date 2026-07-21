package com.courtai.ai.client.impl;

import com.courtai.ai.client.AIClient;
import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.exception.AIConnectionException;
import com.courtai.ai.exception.AITimeoutException;
import com.courtai.ai.registry.RegisteredService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientAIClientImpl implements AIClient {

    private final WebClient aiWebClient;

    @Override
    public boolean validateConnection(RegisteredService service) {
        try {
            log.debug("Validating connection to {}", service.getEndpointUrl());
            // Fast ping or just attempt to hit the base URL.
            // Assuming the AI service has a /health or we just check if it resolves.
            // For now, we assume true if registry says it's ONLINE, but in a real scenario we might ping it.
            return "ONLINE".equalsIgnoreCase(service.getStatus());
        } catch (Exception e) {
            log.warn("Failed to validate connection to {}: {}", service.getEndpointUrl(), e.getMessage());
            return false;
        }
    }

    @Override
    public <T, R> CompletableFuture<R> sendRequest(RegisteredService service, GatewayRequest<T> request, Class<R> responseType) {
        log.info("Sending request {} to AI Service: {}", request.getContext().getRequestId(), service.getName());

        return aiWebClient.post()
                .uri(service.getEndpointUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Correlation-ID", request.getContext().getCorrelationId())
                .header("X-Request-ID", request.getContext().getRequestId())
                .bodyValue(request.getPayload())
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofMillis(service.getTimeoutMs()))
                .onErrorMap(TimeoutException.class, ex -> new AITimeoutException("Request to " + service.getName() + " timed out", ex))
                .onErrorMap(WebClientRequestException.class, ex -> new AIConnectionException("Failed to connect to " + service.getName(), ex))
                .onErrorMap(WebClientResponseException.class, ex -> new AIConnectionException("AI Service returned HTTP " + ex.getStatusCode(), ex))
                .toFuture();
    }
}
