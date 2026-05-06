package com.lloyds.creditcoach.plan.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlanResponse(
        UUID planId,
        UUID customerId,
        String status,
        String confidence,
        Integer scoreAtGeneration,
        Instant generatedAt,
        String disclaimer,
        List<ActionResponse> actions
) {}
