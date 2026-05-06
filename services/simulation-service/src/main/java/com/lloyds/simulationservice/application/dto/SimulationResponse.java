package com.lloyds.simulationservice.application.dto;

import com.lloyds.simulationservice.domain.model.Confidence;
import com.lloyds.simulationservice.domain.model.ScenarioType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SimulationResponse(
    UUID id,
    UUID customerId,
    ScenarioType scenarioType,
    int currentScore,
    int estimatedScore,
    int pointImpact,
    Confidence confidence,
    List<String> factorsChanged,
    String disclaimer,
    Instant createdAt
) {}
