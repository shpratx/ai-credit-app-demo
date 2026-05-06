package com.lloyds.creditcoach.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaActionRepository extends JpaRepository<ImprovementActionEntity, UUID> {

    List<ImprovementActionEntity> findByPlanIdOrderByRankAsc(UUID planId);

    List<ImprovementActionEntity> findByPlanIdAndStatusOrderByRankAsc(UUID planId, String status);

    @Query("SELECT COUNT(a) FROM ImprovementActionEntity a WHERE a.status = 'COMPLETED' AND a.planId IN " +
            "(SELECT p.id FROM ImprovementPlanEntity p WHERE p.customerId = :customerId)")
    long countCompletedByCustomerId(@Param("customerId") UUID customerId);
}
