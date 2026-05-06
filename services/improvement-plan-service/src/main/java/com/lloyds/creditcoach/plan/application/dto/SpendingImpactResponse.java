package com.lloyds.creditcoach.plan.application.dto;

import java.util.List;

public record SpendingImpactResponse(
        String summary,
        List<SpendingCategory> categories
) {
    public record SpendingCategory(
            String category,
            double currentSpend,
            double recommendedSpend,
            String impact
    ) {}
}
