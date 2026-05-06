package com.lloyds.creditcoach.plan.application.command;

import com.lloyds.creditcoach.plan.application.dto.ActionResponse;
import com.lloyds.creditcoach.plan.application.dto.PlanResponse;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import com.lloyds.creditcoach.plan.infrastructure.client.VertexAiPlanClient;
import com.lloyds.creditcoach.plan.infrastructure.messaging.PlanEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshPlanCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(RefreshPlanCommandHandler.class);

    private final PlanRepository planRepository;
    private final ActionRepository actionRepository;
    private final VertexAiPlanClient vertexAiClient;
    private final PlanEventPublisher eventPublisher;

    public RefreshPlanCommandHandler(PlanRepository planRepository,
                                     ActionRepository actionRepository,
                                     VertexAiPlanClient vertexAiClient,
                                     PlanEventPublisher eventPublisher) {
        this.planRepository = planRepository;
        this.actionRepository = actionRepository;
        this.vertexAiClient = vertexAiClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PlanResponse handle(UUID customerId) {
        log.info("Refreshing improvement plan for customerId={}", customerId);

        // Expire existing active plan
        planRepository.findActiveByCustomerId(customerId).ifPresent(existing -> {
            existing.setStatus(ImprovementPlan.PlanStatus.EXPIRED);
            planRepository.save(existing);
        });

        // Call Vertex AI to generate new plan
        List<ImprovementAction> generatedActions = vertexAiClient.generatePlan(customerId);

        // Create new plan
        var plan = new ImprovementPlan();
        plan.setCustomerId(customerId);
        plan.setStatus(generatedActions.isEmpty()
                ? ImprovementPlan.PlanStatus.NO_ACTIONS_NEEDED
                : ImprovementPlan.PlanStatus.ACTIVE);
        plan.setConfidence(ImprovementPlan.Confidence.MEDIUM);
        plan.setScoreAtGeneration(null); // Would be fetched from score service
        plan.setGeneratedAt(Instant.now());
        plan.setDisclaimer("These suggestions are personalised but not financial advice.");

        var savedPlan = planRepository.save(plan);

        // Associate actions with plan
        generatedActions.forEach(a -> a.setPlanId(savedPlan.getId()));
        var savedActions = actionRepository.saveAll(generatedActions);

        eventPublisher.publishPlanGenerated(savedPlan);
        log.info("Plan generated: planId={}, actions={}", savedPlan.getId(), savedActions.size());

        return toPlanResponse(savedPlan, savedActions);
    }

    private PlanResponse toPlanResponse(ImprovementPlan plan, List<ImprovementAction> actions) {
        return new PlanResponse(
                plan.getId(),
                plan.getCustomerId(),
                plan.getStatus().name().toLowerCase(),
                plan.getConfidence().name().toLowerCase(),
                plan.getScoreAtGeneration(),
                plan.getGeneratedAt(),
                plan.getDisclaimer(),
                actions.stream().map(this::toActionResponse).toList()
        );
    }

    private ActionResponse toActionResponse(ImprovementAction a) {
        return new ActionResponse(
                a.getId(), a.getRank(), a.getTitle(), a.getDescription(),
                a.getEstimatedPointImpact(), a.getEstimatedTimeframe(),
                a.getCategory().name().toLowerCase(), a.getStatus().name().toLowerCase(),
                a.getCompletedAt(), a.getExplanation()
        );
    }
}
