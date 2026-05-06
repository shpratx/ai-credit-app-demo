package com.lloyds.creditcoach.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPlanRepository extends JpaRepository<ImprovementPlanEntity, UUID> {

    Optional<ImprovementPlanEntity> findFirstByCustomerIdAndStatusOrderByGeneratedAtDesc(UUID customerId, String status);
}
