package com.lloyds.creditcoach.plan.unit;

import com.lloyds.creditcoach.plan.application.command.RefreshPlanCommandHandler;
import com.lloyds.creditcoach.plan.application.dto.PlanResponse;
import com.lloyds.creditcoach.plan.application.query.GetPlanQueryHandler;
import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPlanQueryHandler")
class GetPlanQueryHandlerTest {

    @Mock private PlanRepository planRepository;
    @Mock private ActionRepository actionRepository;
    @Mock private RefreshPlanCommandHandler refreshHandler;

    private GetPlanQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPlanQueryHandler(planRepository, actionRepository, refreshHandler);
    }

    @Test
    @DisplayName("should return existing active plan with actions")
    void should_returnActivePlan_when_exists() {
        var customerId = UUID.randomUUID();
        var plan = new ImprovementPlan();
        plan.setId(UUID.randomUUID());
        plan.setCustomerId(customerId);
        plan.setStatus(ImprovementPlan.PlanStatus.ACTIVE);
        plan.setConfidence(ImprovementPlan.Confidence.HIGH);
        plan.setGeneratedAt(Instant.now());
        plan.setDisclaimer("Disclaimer");

        var action = new ImprovementAction();
        action.setId(UUID.randomUUID());
        action.setPlanId(plan.getId());
        action.setRank(1);
        action.setTitle("Test");
        action.setDescription("Desc");
        action.setCategory(ImprovementAction.ActionCategory.UTILISATION);
        action.setEstimatedPointImpact(10);
        action.setEstimatedTimeframe("1 month");
        action.setExplanation("Explanation");

        when(planRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.of(plan));
        when(actionRepository.findByPlanId(plan.getId())).thenReturn(List.of(action));

        var result = handler.handle(customerId);

        assertThat(result.status()).isEqualTo("active");
        assertThat(result.actions()).hasSize(1);
        verify(refreshHandler, never()).handle(any());
    }

    @Test
    @DisplayName("should trigger generation when no active plan exists")
    void should_triggerGeneration_when_noPlanExists() {
        var customerId = UUID.randomUUID();
        var generatedResponse = new PlanResponse(
                UUID.randomUUID(), customerId, "active", "medium",
                null, Instant.now(), "Disclaimer", List.of());

        when(planRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.empty());
        when(refreshHandler.handle(customerId)).thenReturn(generatedResponse);

        var result = handler.handle(customerId);

        assertThat(result.status()).isEqualTo("active");
        verify(refreshHandler).handle(customerId);
    }
}
