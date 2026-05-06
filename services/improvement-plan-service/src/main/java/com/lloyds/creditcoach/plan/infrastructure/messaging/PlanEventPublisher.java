package com.lloyds.creditcoach.plan.infrastructure.messaging;

import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;
import com.lloyds.creditcoach.plan.domain.model.Milestone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PlanEventPublisher.class);

    public void publishPlanGenerated(ImprovementPlan plan) {
        log.info("Publishing plan.generated event: planId={}, customerId={}",
                plan.getId(), plan.getCustomerId());
        // Production: publish to Pub/Sub topic credit-coach.plan.generated
    }

    public void publishActionCompleted(ImprovementAction action, UUID customerId) {
        log.info("Publishing action.completed event: actionId={}, customerId={}",
                action.getId(), customerId);
        // Production: publish to Pub/Sub topic credit-coach.action.completed
    }

    public void publishMilestoneAchieved(Milestone milestone) {
        log.info("Publishing milestone.achieved event: milestoneId={}, customerId={}",
                milestone.getId(), milestone.getCustomerId());
        // Production: publish to Pub/Sub topic credit-coach.milestone.achieved
    }
}
