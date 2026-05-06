package com.lloyds.creditcoach.plan.infrastructure.client;

import com.lloyds.creditcoach.plan.application.dto.SpendingImpactResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SpendingAgentClient {

    private static final Logger log = LoggerFactory.getLogger(SpendingAgentClient.class);

    @CircuitBreaker(name = "spendingAgent", fallbackMethod = "fallback")
    @Retry(name = "spendingAgent")
    public SpendingImpactResponse getSpendingImpact(UUID customerId) {
        log.info("Calling spending agent for customerId={}", customerId);
        // Production: call FA spending agent via orchestrator
        return new SpendingImpactResponse(
                "Reducing discretionary spending could improve your credit utilisation.",
                List.of(
                        new SpendingImpactResponse.SpendingCategory("Dining", 450.0, 300.0, "medium"),
                        new SpendingImpactResponse.SpendingCategory("Subscriptions", 120.0, 80.0, "low")
                )
        );
    }

    private SpendingImpactResponse fallback(UUID customerId, Throwable t) {
        log.warn("Spending agent unavailable for customerId={}", customerId);
        return new SpendingImpactResponse("Spending data temporarily unavailable.", List.of());
    }
}
