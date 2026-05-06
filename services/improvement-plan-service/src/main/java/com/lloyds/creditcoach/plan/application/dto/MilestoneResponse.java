package com.lloyds.creditcoach.plan.application.dto;

import java.time.Instant;
import java.util.UUID;

public record MilestoneResponse(
        UUID milestoneId,
        String type,
        String title,
        String description,
        Instant achievedAt,
        Integer scoreAtAchievement,
        Integer targetScore
) {}
