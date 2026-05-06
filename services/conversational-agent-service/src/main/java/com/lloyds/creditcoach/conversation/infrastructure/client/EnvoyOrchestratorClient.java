package com.lloyds.creditcoach.conversation.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EnvoyOrchestratorClient {

    private static final Logger log = LoggerFactory.getLogger(EnvoyOrchestratorClient.class);

    private final WebClient webClient;

    public EnvoyOrchestratorClient(@Value("${app.envoy.base-url:http://localhost:9090}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "envoy", fallbackMethod = "fallback")
    public OrchestratorResponse routeQuery(String message, String sessionId) {
        log.info("Routing query to Envoy orchestrator: sessionId={}", sessionId);
        // In production: POST to Envoy orchestrator
        return new OrchestratorResponse(message, sessionId, true);
    }

    private OrchestratorResponse fallback(String message, String sessionId, Throwable t) {
        log.error("Envoy orchestrator unavailable", t);
        throw new OrchestratorUnavailableException("Envoy orchestrator unavailable", t);
    }
}
