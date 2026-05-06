package com.lloyds.creditcoach.plan.domain.port;

import com.lloyds.creditcoach.plan.domain.model.Milestone;

import java.util.List;
import java.util.UUID;

public interface MilestoneRepository {

    Milestone save(Milestone milestone);

    List<Milestone> findByCustomerId(UUID customerId);

    List<Milestone> findAchievedByCustomerId(UUID customerId);
}
