package com.lloyds.creditcoach.alert.application.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePreferencesRequest(
    @NotNull Boolean utilisationEnabled,
    Integer utilisationThreshold,
    @NotNull Boolean paymentEnabled,
    @NotNull Boolean eligibilityEnabled,
    @NotNull Boolean scoreChangeEnabled,
    @NotNull Boolean allDisabled
) {}
