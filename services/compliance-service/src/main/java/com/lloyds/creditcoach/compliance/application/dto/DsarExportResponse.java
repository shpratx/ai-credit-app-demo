package com.lloyds.creditcoach.compliance.application.dto;

import java.time.Instant;
import java.util.UUID;

public record DsarExportResponse(
    UUID id,
    UUID customerId,
    String status,
    Instant requestedAt,
    Instant completedAt,
    String downloadUrl
) {}
