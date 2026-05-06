package com.lloyds.simulationservice.application.command;

import com.lloyds.simulationservice.domain.model.ScenarioType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record RunSimulationCommand(
    @NotNull(message = "Customer ID is required") UUID customerId,
    @NotNull(message = "Scenario type is required") ScenarioType scenarioType,
    @Positive(message = "Amount must be positive") BigDecimal amount,
    UUID accountId
) {}
