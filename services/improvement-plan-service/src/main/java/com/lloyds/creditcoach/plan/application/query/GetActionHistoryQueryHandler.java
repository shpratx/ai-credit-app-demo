package com.lloyds.creditcoach.plan.application.query;

import com.lloyds.creditcoach.plan.application.dto.ActionResponse;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetActionHistoryQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetActionHistoryQueryHandler.class);

    private final PlanRepository planRepository;
    private final ActionRepository actionRepository;

    public GetActionHistoryQueryHandler(PlanRepository planRepository,
                                        ActionRepository actionRepository) {
        this.planRepository = planRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional(readOnly = true)
    public List<ActionResponse> handle(UUID customerId, String status) {
        log.info("Getting action history for customerId={}, status={}", customerId, status);

        var plan = planRepository.findActiveByCustomerId(customerId);
        if (plan.isEmpty()) {
            return List.of();
        }

        List<ImprovementAction> actions;
        if ("all".equalsIgnoreCase(status) || status == null) {
            actions = actionRepository.findByPlanId(plan.get().getId());
        } else {
            var actionStatus = ImprovementAction.ActionStatus.valueOf(status.toUpperCase());
            actions = actionRepository.findByPlanIdAndStatus(plan.get().getId(), actionStatus);
        }

        return actions.stream().map(a -> new ActionResponse(
                a.getId(), a.getRank(), a.getTitle(), a.getDescription(),
                a.getEstimatedPointImpact(), a.getEstimatedTimeframe(),
                a.getCategory().name().toLowerCase(), a.getStatus().name().toLowerCase(),
                a.getCompletedAt(), a.getExplanation()
        )).toList();
    }
}
