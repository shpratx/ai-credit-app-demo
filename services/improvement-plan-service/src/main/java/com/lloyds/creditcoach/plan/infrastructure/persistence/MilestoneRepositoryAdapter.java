package com.lloyds.creditcoach.plan.infrastructure.persistence;

import com.lloyds.creditcoach.plan.domain.model.Milestone;
import com.lloyds.creditcoach.plan.domain.port.MilestoneRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MilestoneRepositoryAdapter implements MilestoneRepository {

    private final JpaMilestoneRepository jpaRepository;

    public MilestoneRepositoryAdapter(JpaMilestoneRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Milestone save(Milestone milestone) {
        return toDomain(jpaRepository.save(toEntity(milestone)));
    }

    @Override
    public List<Milestone> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByAchievedAtDesc(customerId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Milestone> findAchievedByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdAndAchievedAtIsNotNullOrderByAchievedAtDesc(customerId)
                .stream().map(this::toDomain).toList();
    }

    private MilestoneEntity toEntity(Milestone m) {
        var entity = new MilestoneEntity();
        entity.setId(m.getId());
        entity.setCustomerId(m.getCustomerId());
        entity.setType(m.getType().name());
        entity.setTitle(m.getTitle());
        entity.setDescription(m.getDescription());
        entity.setAchievedAt(m.getAchievedAt());
        entity.setScoreAtAchievement(m.getScoreAtAchievement());
        entity.setTargetScore(m.getTargetScore());
        return entity;
    }

    private Milestone toDomain(MilestoneEntity entity) {
        var m = new Milestone();
        m.setId(entity.getId());
        m.setCustomerId(entity.getCustomerId());
        m.setType(Milestone.MilestoneType.valueOf(entity.getType()));
        m.setTitle(entity.getTitle());
        m.setDescription(entity.getDescription());
        m.setAchievedAt(entity.getAchievedAt());
        m.setScoreAtAchievement(entity.getScoreAtAchievement());
        m.setTargetScore(entity.getTargetScore());
        return m;
    }
}
