package com.lloyds.creditcoach.plan.domain.port;

import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActionRepository {

    ImprovementAction save(ImprovementAction action);

    List<ImprovementAction> saveAll(List<ImprovementAction> actions);

    Optional<ImprovementAction> findById(UUID id);

    List<ImprovementAction> findByPlanId(UUID planId);

    List<ImprovementAction> findByPlanIdAndStatus(UUID planId, ImprovementAction.ActionStatus status);

    long countCompletedByCustomerId(UUID customerId);
}
