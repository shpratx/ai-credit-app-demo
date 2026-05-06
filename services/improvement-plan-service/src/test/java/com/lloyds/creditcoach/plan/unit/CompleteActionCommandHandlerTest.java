package com.lloyds.creditcoach.plan.unit;

import com.lloyds.creditcoach.plan.application.command.CompleteActionCommandHandler;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.MilestoneRepository;
import com.lloyds.creditcoach.plan.infrastructure.messaging.PlanEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteActionCommandHandler")
class CompleteActionCommandHandlerTest {

    @Mock private ActionRepository actionRepository;
    @Mock private MilestoneRepository milestoneRepository;
    @Mock private PlanEventPublisher eventPublisher;

    private CompleteActionCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CompleteActionCommandHandler(actionRepository, milestoneRepository, eventPublisher);
    }

    @Test
    @DisplayName("should mark action as completed")
    void should_markActionCompleted_when_validAction() {
        var customerId = UUID.randomUUID();
        var actionId = UUID.randomUUID();
        var action = new ImprovementAction();
        action.setId(actionId);
        action.setPlanId(UUID.randomUUID());
        action.setRank(1);
        action.setTitle("Test action");
        action.setDescription("Test");
        action.setCategory(ImprovementAction.ActionCategory.UTILISATION);
        action.setStatus(ImprovementAction.ActionStatus.NOT_STARTED);
        action.setEstimatedPointImpact(10);
        action.setEstimatedTimeframe("1 month");
        action.setExplanation("Test explanation");

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(actionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(actionRepository.countCompletedByCustomerId(customerId)).thenReturn(2L);

        var result = handler.handle(customerId, actionId);

        assertThat(result.status()).isEqualTo("completed");
        assertThat(result.completedAt()).isNotNull();
        verify(eventPublisher).publishActionCompleted(any(), eq(customerId));
    }

    @Test
    @DisplayName("should award first improvement milestone on first completion")
    void should_awardMilestone_when_firstCompletion() {
        var customerId = UUID.randomUUID();
        var actionId = UUID.randomUUID();
        var action = new ImprovementAction();
        action.setId(actionId);
        action.setPlanId(UUID.randomUUID());
        action.setRank(1);
        action.setTitle("Test");
        action.setDescription("Test");
        action.setCategory(ImprovementAction.ActionCategory.PAYMENT_HISTORY);
        action.setEstimatedPointImpact(5);
        action.setEstimatedTimeframe("Immediate");
        action.setExplanation("Explanation");

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(actionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(actionRepository.countCompletedByCustomerId(customerId)).thenReturn(1L);
        when(milestoneRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        handler.handle(customerId, actionId);

        verify(milestoneRepository).save(any());
        verify(eventPublisher).publishMilestoneAchieved(any());
    }

    @Test
    @DisplayName("should throw when action not found")
    void should_throw_when_actionNotFound() {
        var actionId = UUID.randomUUID();
        when(actionRepository.findById(actionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(UUID.randomUUID(), actionId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
