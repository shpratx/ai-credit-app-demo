package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ChangeExplanationResponse(
        UUID customerId,
        Integer previousScore,
        Integer currentScore,
        Integer totalChange,
        String changeDirection,
        List<ContributorDto> contributors,
        LocalDate periodStart,
        LocalDate periodEnd
) {}

