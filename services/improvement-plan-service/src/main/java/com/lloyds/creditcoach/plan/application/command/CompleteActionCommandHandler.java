package com.lloyds.creditcoach.plan.application.command;

import com.lloyds.creditcoach.plan.application.dto.ActionResponse;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.model.Milestone;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.MilestoneRepository;
import com.lloyds.creditcoach.plan.infrastructure.messaging.PlanEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CompleteActionCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CompleteActionCommandHandler.class);

    private final ActionRepository actionRepository;
    private final MilestoneRepository milestoneRepository;
    private final PlanEventPublisher eventPublisher;

    public CompleteActionCommandHandler(ActionRepository actionRepository,
                                        MilestoneRepository milestoneRepository,
                                        PlanEventPublisher eventPublisher) {
        this.actionRepository = actionRepository;
        this.milestoneRepository = milestoneRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActionResponse handle(UUID customerId, UUID actionId) {
        log.info("Completing action: actionId={}, customerId={}", actionId, customerId);

        var action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionId));

        action.setStatus(ImprovementAction.ActionStatus.COMPLETED);
        action.setCompletedAt(Instant.now());
        var saved = actionRepository.save(action);

        eventPublisher.publishActionCompleted(saved, customerId);
        checkAndAwardMilestones(customerId);

        log.info("Action completed: actionId={}", actionId);
        return new ActionResponse(
                saved.getId(), saved.getRank(), saved.getTitle(), saved.getDescription(),
                saved.getEstimatedPointImpact(), saved.getEstimatedTimeframe(),
                saved.getCategory().name().toLowerCase(), saved.getStatus().name().toLowerCase(),
                saved.getCompletedAt(), saved.getExplanation()
        );
    }

    private void checkAndAwardMilestones(UUID customerId) {
        long completedCount = actionRepository.countCompletedByCustomerId(customerId);

        if (completedCount == 1) {
            var milestone = new Milestone();
            milestone.setCustomerId(customerId);
            milestone.setType(Milestone.MilestoneType.FIRST_IMPROVEMENT);
            milestone.setTitle("First Step Taken");
            milestone.setDescription("You completed your first improvement action!");
            milestone.setAchievedAt(Instant.now());
            milestoneRepository.save(milestone);
            eventPublisher.publishMilestoneAchieved(milestone);
        }
    }
}
