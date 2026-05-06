package com.lloyds.creditcoach.compliance.application.dto;

import java.util.List;
import java.util.UUID;

public record DecisionExplanationResponse(
    UUID offerId,
    UUID customerId,
    String decision,
    List<FactorDto> factors,
    String humanReviewOption
) {
    public record FactorDto(String name, String impact, String description) {}
}
