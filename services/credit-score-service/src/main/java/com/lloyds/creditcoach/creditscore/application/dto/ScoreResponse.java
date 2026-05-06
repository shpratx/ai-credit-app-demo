package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ScoreResponse(
        UUID customerId,
        String provider,
        Integer score,
        Integer maxScore,
        String band,
        Integer previousScore,
        Integer change,
        String changeDirection,
        Instant retrievedAt,
        Boolean isStale,
        String source
) {}
