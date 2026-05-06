package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MultiBureauResponse(
    UUID customerId,
    List<BureauScore> bureauScores,
    Instant comparedAt
) {
    public record BureauScore(
        String provider,
        int score,
        int maxScore,
        String band,
        int normalisedScore,
        Instant retrievedAt
    ) {}
}
