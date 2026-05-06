package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FactorsResponse(
        UUID customerId,
        List<ScoreFactorDto> factors,
        int positiveCount,
        int negativeCount,
        Instant retrievedAt
) {}
