package com.lloyds.creditcoach.alert.domain.port;

import com.lloyds.creditcoach.alert.domain.model.AlertPreference;

import java.util.Optional;
import java.util.UUID;

public interface AlertPreferenceRepository {
    Optional<AlertPreference> findByCustomerId(UUID customerId);
    AlertPreference save(AlertPreference preference);
}
