package com.lloyds.creditcoach.alert.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
    UUID id,
    UUID customerId,
    String type,
    String title,
    String message,
    String severity,
    String status,
    Instant createdAt
) {}
