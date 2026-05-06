package com.lloyds.creditcoach.plan.infrastructure.client;

import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VertexAiPlanClient {

    private static final Logger log = LoggerFactory.getLogger(VertexAiPlanClient.class);

    @CircuitBreaker(name = "vertexai", fallbackMethod = "fallback")
    @Retry(name = "vertexai")
    public List<ImprovementAction> generatePlan(UUID customerId) {
        log.info("Calling Vertex AI to generate plan for customerId={}", customerId);
        // Production: call Vertex AI endpoint
        // Placeholder returning sample actions for compilation
        var action1 = new ImprovementAction();
        action1.setRank(1);
        action1.setTitle("Reduce credit utilisation below 30%");
        action1.setDescription("Pay down your credit card balance to use less than 30% of your limit.");
        action1.setEstimatedPointImpact(25);
        action1.setEstimatedTimeframe("1-3 months");
        action1.setCategory(ImprovementAction.ActionCategory.UTILISATION);
        action1.setExplanation("High utilisation signals over-reliance on credit.");

        var action2 = new ImprovementAction();
        action2.setRank(2);
        action2.setTitle("Set up direct debit for minimum payments");
        action2.setDescription("Ensure you never miss a payment by automating minimum payments.");
        action2.setEstimatedPointImpact(15);
        action2.setEstimatedTimeframe("Immediate");
        action2.setCategory(ImprovementAction.ActionCategory.PAYMENT_HISTORY);
        action2.setExplanation("Payment history is the largest factor in your score.");

        return List.of(action1, action2);
    }

    private List<ImprovementAction> fallback(UUID customerId, Throwable t) {
        log.warn("Vertex AI unavailable for plan generation, customerId={}", customerId);
        return List.of();
    }
}
