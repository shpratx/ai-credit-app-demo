package com.lloyds.creditcoach.alert.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAlertPreferenceRepository extends JpaRepository<AlertPreferenceEntity, UUID> {
    Optional<AlertPreferenceEntity> findByCustomerId(UUID customerId);
}
