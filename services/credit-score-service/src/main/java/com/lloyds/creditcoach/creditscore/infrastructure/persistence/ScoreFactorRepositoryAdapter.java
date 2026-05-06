package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import com.lloyds.creditcoach.creditscore.domain.model.ScoreFactor;
import com.lloyds.creditcoach.creditscore.domain.port.ScoreFactorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ScoreFactorRepositoryAdapter implements ScoreFactorRepository {

    private final JpaScoreFactorRepository jpaRepository;

    public ScoreFactorRepositoryAdapter(JpaScoreFactorRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ScoreFactor> saveAll(List<ScoreFactor> factors) {
        var entities = factors.stream().map(this::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ScoreFactor> findByScoreId(UUID scoreId) {
        return jpaRepository.findByScoreId(scoreId).stream().map(this::toDomain).toList();
    }

    private ScoreFactorEntity toEntity(ScoreFactor f) {
        var e = new ScoreFactorEntity();
        e.setId(f.getId());
        e.setScoreId(f.getScoreId());
        e.setCategory(f.getCategory());
        e.setImpact(f.getImpact());
        e.setDirection(f.getDirection());
        e.setTitle(f.getTitle());
        e.setDescription(f.getDescription());
        e.setWeightingPercent(f.getWeightingPercent());
        e.setCreatedAt(f.getCreatedAt());
        return e;
    }

    private ScoreFactor toDomain(ScoreFactorEntity e) {
        var f = new ScoreFactor();
        f.setId(e.getId());
        f.setScoreId(e.getScoreId());
        f.setCategory(e.getCategory());
        f.setImpact(e.getImpact());
        f.setDirection(e.getDirection());
        f.setTitle(e.getTitle());
        f.setDescription(e.getDescription());
        f.setWeightingPercent(e.getWeightingPercent());
        f.setCreatedAt(e.getCreatedAt());
        return f;
    }
}
