package com.lloyds.simulationservice.unit;

import com.lloyds.simulationservice.application.command.RunSimulationCommand;
import com.lloyds.simulationservice.application.command.RunSimulationCommandHandler;
import com.lloyds.simulationservice.application.dto.SimulationResponse;
import com.lloyds.simulationservice.domain.exception.BusinessRuleException;
import com.lloyds.simulationservice.domain.exception.SimulationTimeoutException;
import com.lloyds.simulationservice.domain.model.Confidence;
import com.lloyds.simulationservice.domain.model.ScenarioType;
import com.lloyds.simulationservice.domain.model.SimulationResult;
import com.lloyds.simulationservice.domain.port.SimulationRepository;
import com.lloyds.simulationservice.infrastructure.client.VertexAiSimulationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RunSimulationCommandHandler")
class RunSimulationCommandHandlerTest {

    @Mock private VertexAiSimulationClient vertexAiClient;
    @Mock private SimulationRepository repository;

    private RunSimulationCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RunSimulationCommandHandler(vertexAiClient, repository);
    }

    @Test
    @DisplayName("should return simulation result when valid scenario")
    void should_returnSimulationResult_when_validScenario() {
        var command = new RunSimulationCommand(UUID.randomUUID(), ScenarioType.PAY_DEBT, new BigDecimal("5000"), UUID.randomUUID());

        var aiResult = new SimulationResult();
        aiResult.setCurrentScore(650);
        aiResult.setEstimatedScore(680);
        aiResult.setPointImpact(30);
        aiResult.setConfidence(Confidence.HIGH);
        aiResult.setFactorsChanged(List.of("utilisation", "payment_history"));

        when(vertexAiClient.runSimulation(command)).thenReturn(aiResult);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SimulationResponse result = handler.handle(command);

        assertThat(result.currentScore()).isEqualTo(650);
        assertThat(result.estimatedScore()).isEqualTo(680);
        assertThat(result.pointImpact()).isEqualTo(30);
        assertThat(result.disclaimer()).isNotBlank();
    }

    @Test
    @DisplayName("should throw SimulationTimeoutException when Vertex AI times out")
    void should_throwTimeout_when_vertexAiTimesOut() {
        var command = new RunSimulationCommand(UUID.randomUUID(), ScenarioType.REDUCE_UTILISATION, new BigDecimal("2000"), null);

        when(vertexAiClient.runSimulation(command))
                .thenThrow(new SimulationTimeoutException("Simulation taking too long. Please try again shortly."));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(SimulationTimeoutException.class)
                .hasMessageContaining("Simulation taking too long");
    }

    @Test
    @DisplayName("should reject contradictory scenario when miss_payment without accountId")
    void should_rejectScenario_when_missPaymentWithoutAccountId() {
        var command = new RunSimulationCommand(UUID.randomUUID(), ScenarioType.MISS_PAYMENT, null, null);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Account ID is required");
    }
}
