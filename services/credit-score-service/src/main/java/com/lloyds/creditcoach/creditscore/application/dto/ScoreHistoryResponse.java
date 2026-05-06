package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ScoreHistoryResponse(
        UUID customerId,
        String provider,
        List<DataPointDto> dataPoints,
        Integer totalMonths
) {}

