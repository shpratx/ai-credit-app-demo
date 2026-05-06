package com.lloyds.creditcoach.creditscore.infrastructure.client;

public record CraFactorResponse(
        String category,
        String impact,
        String direction,
        String title,
        String description,
        Integer weightingPercent
) {}
