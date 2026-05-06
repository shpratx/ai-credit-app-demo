package com.lloyds.creditcoach.plan.infrastructure.persistence;

import com.lloyds.creditcoach.plan.domain.model.ImprovementPlan;
import com.lloyds.creditcoach.plan.domain.port.PlanRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlanRepositoryAdapter implements PlanRepository {

    private final JpaPlanRepository jpaRepository;

    public PlanRepositoryAdapter(JpaPlanRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImprovementPlan save(ImprovementPlan plan) {
        var entity = toEntity(plan);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ImprovementPlan> findActiveByCustomerId(UUID customerId) {
        return jpaRepository.findFirstByCustomerIdAndStatusOrderByGeneratedAtDesc(customerId, "ACTIVE")
                .map(this::toDomain);
    }

    @Override
    public Optional<ImprovementPlan> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private ImprovementPlanEntity toEntity(ImprovementPlan plan) {
        var entity = new ImprovementPlanEntity();
        entity.setId(plan.getId());
        entity.setCustomerId(plan.getCustomerId());
        entity.setStatus(plan.getStatus().name());
        entity.setConfidence(plan.getConfidence() != null ? plan.getConfidence().name() : null);
        entity.setScoreAtGeneration(plan.getScoreAtGeneration());
        entity.setGeneratedAt(plan.getGeneratedAt());
        entity.setDisclaimer(plan.getDisclaimer());
        return entity;
    }

    private ImprovementPlan toDomain(ImprovementPlanEntity entity) {
        var plan = new ImprovementPlan();
        plan.setId(entity.getId());
        plan.setCustomerId(entity.getCustomerId());
        plan.setStatus(ImprovementPlan.PlanStatus.valueOf(entity.getStatus()));
        plan.setConfidence(entity.getConfidence() != null
                ? ImprovementPlan.Confidence.valueOf(entity.getConfidence()) : null);
        plan.setScoreAtGeneration(entity.getScoreAtGeneration());
        plan.setGeneratedAt(entity.getGeneratedAt());
        plan.setDisclaimer(entity.getDisclaimer());
        return plan;
    }
}
