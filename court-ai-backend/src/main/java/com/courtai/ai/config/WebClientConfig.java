package com.courtai.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuration for the Spring WebClient used by the AI Gateway.
 */
@Configuration
public class WebClientConfig {

    private final AIProperties aiProperties;

    public WebClientConfig(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Bean
    public WebClient aiWebClient(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(aiProperties.getReadTimeout()));

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
