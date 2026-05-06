package com.lloyds.creditcoach.compliance.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BreathingSpaceResponse(
    UUID id,
    UUID customerId,
    LocalDate startDate,
    LocalDate endDate,
    String status
) {}
