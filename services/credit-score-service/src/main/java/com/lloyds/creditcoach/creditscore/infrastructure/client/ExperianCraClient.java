package com.lloyds.creditcoach.creditscore.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ExperianCraClient {

    private static final Logger log = LoggerFactory.getLogger(ExperianCraClient.class);

    @CircuitBreaker(name = "experian", fallbackMethod = "fallback")
    @Retry(name = "experian")
    @TimeLimiter(name = "experian")
    public CraResponse retrieveScore(UUID customerId) {
        log.info("Calling Experian CRA for customerId={}", customerId);
        // In production: HTTP call to Experian API
        // Placeholder returning mock data for compilation
        return new CraResponse(
                742, 98,
                List.of(
                        new CraFactorResponse("payment_history", "high", "positive",
                                "Payment History", "You have made all payments on time", 35),
                        new CraFactorResponse("utilisation", "medium", "negative",
                                "Credit Utilisation", "Your credit utilisation is at 62%", 30)
                )
        );
    }

    private CraResponse fallback(UUID customerId, Throwable t) {
        log.error("Experian CRA circuit breaker open for customerId={}", customerId, t);
        throw new CraUnavailableException("Experian CRA unavailable", t);
    }
}
