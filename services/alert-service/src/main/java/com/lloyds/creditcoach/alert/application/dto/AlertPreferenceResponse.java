package com.lloyds.creditcoach.alert.application.dto;

import java.util.UUID;

public record AlertPreferenceResponse(
    UUID id,
    UUID customerId,
    boolean utilisationEnabled,
    int utilisationThreshold,
    boolean paymentEnabled,
    boolean eligibilityEnabled,
    boolean scoreChangeEnabled,
    boolean allDisabled
) {}
