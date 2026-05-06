package com.lloyds.creditcoach.plan.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ActionResponse(
        UUID actionId,
        int rank,
        String title,
        String description,
        Integer estimatedPointImpact,
        String estimatedTimeframe,
        String category,
        String status,
        Instant completedAt,
        String explanation
) {}
