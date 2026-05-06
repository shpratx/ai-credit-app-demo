package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ScoreTrendResponse(
        UUID customerId,
        String provider,
        int months,
        Integer currentScore,
        Integer lowestScore,
        Integer highestScore,
        Integer averageScore,
        String overallTrend,
        List<TrendDataPoint> dataPoints,
        List<TrendAnnotation> annotations
) {
    public record TrendDataPoint(
            LocalDate date,
            Integer score,
            String band
    ) {}

    public record TrendAnnotation(
            LocalDate date,
            String label,
            String type
    ) {}
}
