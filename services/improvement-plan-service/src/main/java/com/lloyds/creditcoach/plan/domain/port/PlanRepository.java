package com.lloyds.creditcoach.plan.domain.port;

import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository {

    ImprovementPlan save(ImprovementPlan plan);

    Optional<ImprovementPlan> findActiveByCustomerId(UUID customerId);

    Optional<ImprovementPlan> findById(UUID id);
}
