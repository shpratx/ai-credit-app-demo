package com.lloyds.creditcoach.plan.application.query;

import com.lloyds.creditcoach.plan.application.command.RefreshPlanCommandHandler;
import com.lloyds.creditcoach.plan.application.dto.ActionResponse;
import com.lloyds.creditcoach.plan.application.dto.PlanResponse;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPlanQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetPlanQueryHandler.class);

    private final PlanRepository planRepository;
    private final ActionRepository actionRepository;
    private final RefreshPlanCommandHandler refreshHandler;

    public GetPlanQueryHandler(PlanRepository planRepository,
                               ActionRepository actionRepository,
                               RefreshPlanCommandHandler refreshHandler) {
        this.planRepository = planRepository;
        this.actionRepository = actionRepository;
        this.refreshHandler = refreshHandler;
    }

    @Transactional(readOnly = true)
    public PlanResponse handle(UUID customerId) {
        log.info("Getting improvement plan for customerId={}", customerId);

        var planOpt = planRepository.findActiveByCustomerId(customerId);
        if (planOpt.isEmpty()) {
            log.info("No active plan found, triggering generation for customerId={}", customerId);
            return refreshHandler.handle(customerId);
        }

        var plan = planOpt.get();
        var actions = actionRepository.findByPlanId(plan.getId());

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
