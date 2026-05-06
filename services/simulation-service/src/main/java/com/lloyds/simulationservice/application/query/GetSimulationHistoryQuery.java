package com.lloyds.simulationservice.application.query;

import java.util.UUID;

public record GetSimulationHistoryQuery(UUID customerId, int page, int size) {}
