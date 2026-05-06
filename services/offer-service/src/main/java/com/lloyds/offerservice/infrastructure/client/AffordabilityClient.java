package com.lloyds.offerservice.infrastructure.client;

import com.lloyds.offerservice.application.dto.AffordabilityResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CONC 5.2A: affordability assessment before offer generation.
 */
@Component
public class AffordabilityClient {

    private static final Logger log = LoggerFactory.getLogger(AffordabilityClient.class);
    private final RestClient restClient;

    public AffordabilityClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("${AFFORDABILITY_SERVICE_URL:http://localhost:8093}").build();
    }

    @CircuitBreaker(name = "affordabilityService", fallbackMethod = "affordabilityFallback")
    public AffordabilityResult checkAffordability(UUID customerId, BigDecimal amount) {
        log.info("Running CONC 5.2A affordability check for customer: {}", customerId);
        return restClient.post()
                .uri("/api/v1/affordability/check")
                .body(new AffordabilityRequest(customerId, amount))
                .retrieve()
                .body(AffordabilityResult.class);
    }

    private AffordabilityResult affordabilityFallback(UUID customerId, BigDecimal amount, Throwable t) {
        log.warn("Affordability service unavailable, rejecting by default: {}", t.getMessage());
        return new AffordabilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private record AffordabilityRequest(UUID customerId, BigDecimal amount) {}
}
