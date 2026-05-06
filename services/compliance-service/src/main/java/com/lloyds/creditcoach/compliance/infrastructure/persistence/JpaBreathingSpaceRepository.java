package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBreathingSpaceRepository extends JpaRepository<BreathingSpaceEntity, UUID> {

    @Query("SELECT b FROM BreathingSpaceEntity b WHERE b.customerId = :customerId AND b.status = 'ACTIVE' AND b.endDate >= CURRENT_DATE")
    Optional<BreathingSpaceEntity> findActiveByCustomerId(UUID customerId);
}
