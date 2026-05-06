package com.lloyds.creditcoach.plan.infrastructure.persistence;

import com.lloyds.creditcoach.plan.domain.model.ImprovementAction;
import com.lloyds.creditcoach.plan.domain.port.ActionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ActionRepositoryAdapter implements ActionRepository {

    private final JpaActionRepository jpaRepository;

    public ActionRepositoryAdapter(JpaActionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImprovementAction save(ImprovementAction action) {
        return toDomain(jpaRepository.save(toEntity(action)));
    }

    @Override
    public List<ImprovementAction> saveAll(List<ImprovementAction> actions) {
        var entities = actions.stream().map(this::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ImprovementAction> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ImprovementAction> findByPlanId(UUID planId) {
        return jpaRepository.findByPlanIdOrderByRankAsc(planId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ImprovementAction> findByPlanIdAndStatus(UUID planId, ImprovementAction.ActionStatus status) {
        return jpaRepository.findByPlanIdAndStatusOrderByRankAsc(planId, status.name())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countCompletedByCustomerId(UUID customerId) {
        return jpaRepository.countCompletedByCustomerId(customerId);
    }

    private ImprovementActionEntity toEntity(ImprovementAction action) {
        var entity = new ImprovementActionEntity();
        entity.setId(action.getId());
        entity.setPlanId(action.getPlanId());
        entity.setRank(action.getRank());
        entity.setTitle(action.getTitle());
        entity.setDescription(action.getDescription());
        entity.setEstimatedPointImpact(action.getEstimatedPointImpact());
        entity.setEstimatedTimeframe(action.getEstimatedTimeframe());
        entity.setCategory(action.getCategory().name());
        entity.setStatus(action.getStatus().name());
        entity.setCompletedAt(action.getCompletedAt());
        entity.setExplanation(action.getExplanation());
        return entity;
    }

    private ImprovementAction toDomain(ImprovementActionEntity entity) {
        var action = new ImprovementAction();
        action.setId(entity.getId());
        action.setPlanId(entity.getPlanId());
        action.setRank(entity.getRank());
        action.setTitle(entity.getTitle());
        action.setDescription(entity.getDescription());
        action.setEstimatedPointImpact(entity.getEstimatedPointImpact());
        action.setEstimatedTimeframe(entity.getEstimatedTimeframe());
        action.setCategory(ImprovementAction.ActionCategory.valueOf(entity.getCategory()));
        action.setStatus(ImprovementAction.ActionStatus.valueOf(entity.getStatus()));
        action.setCompletedAt(entity.getCompletedAt());
        action.setExplanation(entity.getExplanation());
        return action;
    }
}
