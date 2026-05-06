package com.lloyds.creditcoach.plan.unit;

import com.lloyds.creditcoach.plan.application.command.RefreshPlanCommandHandler;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import com.lloyds.creditcoach.plan.infrastructure.client.VertexAiPlanClient;
import com.lloyds.creditcoach.plan.infrastructure.messaging.PlanEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshPlanCommandHandler")
class RefreshPlanCommandHandlerTest {

    @Mock private PlanRepository planRepository;
    @Mock private ActionRepository actionRepository;
    @Mock private VertexAiPlanClient vertexAiClient;
    @Mock private PlanEventPublisher eventPublisher;

    private RefreshPlanCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RefreshPlanCommandHandler(planRepository, actionRepository, vertexAiClient, eventPublisher);
    }

    @Test
    @DisplayName("should generate new plan when Vertex AI returns actions")
    void should_generatePlan_when_vertexAiReturnsActions() {
        var customerId = UUID.randomUUID();
        var action = new ImprovementAction();
        action.setRank(1);
        action.setTitle("Reduce utilisation");
        action.setDescription("Pay down balance");
        action.setCategory(ImprovementAction.ActionCategory.UTILISATION);
        action.setEstimatedPointImpact(20);
        action.setEstimatedTimeframe("1 month");
        action.setExplanation("High utilisation hurts score");

        when(planRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.empty());
        when(vertexAiClient.generatePlan(customerId)).thenReturn(List.of(action));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(actionRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = handler.handle(customerId);

        assertThat(result.status()).isEqualTo("active");
        assertThat(result.actions()).hasSize(1);
        verify(eventPublisher).publishPlanGenerated(any());
    }

    @Test
    @DisplayName("should expire existing plan before generating new one")
    void should_expireExistingPlan_when_refreshing() {
        var customerId = UUID.randomUUID();
        var existingPlan = new ImprovementPlan();
        existingPlan.setCustomerId(customerId);
        existingPlan.setStatus(ImprovementPlan.PlanStatus.ACTIVE);

        when(planRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.of(existingPlan));
        when(vertexAiClient.generatePlan(customerId)).thenReturn(List.of());
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(actionRepository.saveAll(any())).thenReturn(List.of());

        handler.handle(customerId);

        verify(planRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("should set status to no_actions_needed when Vertex AI returns empty")
    void should_setNoActionsNeeded_when_noActionsGenerated() {
        var customerId = UUID.randomUUID();

        when(planRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.empty());
        when(vertexAiClient.generatePlan(customerId)).thenReturn(List.of());
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(actionRepository.saveAll(any())).thenReturn(List.of());

        var result = handler.handle(customerId);

        assertThat(result.status()).isEqualTo("no_actions_needed");
    }
}
