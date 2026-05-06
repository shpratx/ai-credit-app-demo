package com.lloyds.creditcoach.creditscore.application.dto;

public record ScoreFactorDto(
        String factorId,
        String category,
        String impact,
        String direction,
        String title,
        String description,
        Integer weightingPercent
) {}
