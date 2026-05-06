package com.lloyds.creditcoach.compliance.domain.port;

import com.lloyds.creditcoach.compliance.domain.model.BreathingSpace;

import java.util.Optional;
import java.util.UUID;

public interface BreathingSpaceRepository {
    BreathingSpace save(BreathingSpace breathingSpace);
    Optional<BreathingSpace> findActiveByCustomerId(UUID customerId);
}
