package com.lloyds.simulationservice.application.command;

import com.lloyds.simulationservice.application.dto.SimulationResponse;
import com.lloyds.simulationservice.domain.exception.BusinessRuleException;
import com.lloyds.simulationservice.domain.model.ScenarioType;
import com.lloyds.simulationservice.domain.model.SimulationResult;
import com.lloyds.simulationservice.domain.port.SimulationRepository;
import com.lloyds.simulationservice.infrastructure.client.VertexAiSimulationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RunSimulationCommandHandler implements CommandHandler<RunSimulationCommand, SimulationResponse> {

    private static final Logger log = LoggerFactory.getLogger(RunSimulationCommandHandler.class);
    private static final String DISCLAIMER = "This simulation is an estimate based on historical patterns. " +
            "Actual score changes may vary depending on multiple factors including lender reporting timelines.";

    private final VertexAiSimulationClient vertexAiClient;
    private final SimulationRepository repository;

    public RunSimulationCommandHandler(VertexAiSimulationClient vertexAiClient, SimulationRepository repository) {
        this.vertexAiClient = vertexAiClient;
        this.repository = repository;
    }

    @Override
    @Transactional
    public SimulationResponse handle(RunSimulationCommand command) {
        log.info("Running simulation for customer: {}, scenario: {}", command.customerId(), command.scenarioType());

        validateScenario(command);

        SimulationResult result = vertexAiClient.runSimulation(command);
        result.setCustomerId(command.customerId());
        result.setScenarioType(command.scenarioType());
        result.setDisclaimer(DISCLAIMER);

        SimulationResult saved = repository.save(result);
        log.info("Simulation completed: id={}, impact={}", saved.getId(), saved.getPointImpact());

        return toResponse(saved);
    }

    private void validateScenario(RunSimulationCommand command) {
        if (command.scenarioType() == ScenarioType.MISS_PAYMENT && command.accountId() == null) {
            throw new BusinessRuleException("INVALID_SCENARIO", "Account ID is required for miss_payment scenario");
        }
        if (command.scenarioType() == ScenarioType.PAY_DEBT && command.amount() == null) {
            throw new BusinessRuleException("INVALID_SCENARIO", "Amount is required for pay_debt scenario");
        }
    }

    private SimulationResponse toResponse(SimulationResult r) {
        return new SimulationResponse(
                r.getId(), r.getCustomerId(), r.getScenarioType(),
                r.getCurrentScore(), r.getEstimatedScore(), r.getPointImpact(),
                r.getConfidence(), r.getFactorsChanged(), r.getDisclaimer(), r.getCreatedAt());
    }
}
