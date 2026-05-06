package com.lloyds.creditcoach.consent.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ConsentResponse(
        UUID consentId,
        UUID customerId,
        String craProvider,
        String status,
        Instant grantedAt,
        Instant withdrawnAt
) {}
