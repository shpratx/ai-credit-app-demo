package com.lloyds.simulationservice.infrastructure.persistence.repository;

import com.lloyds.simulationservice.domain.model.SimulationResult;
import com.lloyds.simulationservice.domain.port.SimulationRepository;
import com.lloyds.simulationservice.infrastructure.persistence.entity.SimulationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class SimulationRepositoryAdapter implements SimulationRepository {

    private final SimulationJpaRepository jpaRepository;

    public SimulationRepositoryAdapter(SimulationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SimulationResult save(SimulationResult result) {
        SimulationEntity entity = toEntity(result);
        SimulationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Page<SimulationResult> findByCustomerId(UUID customerId, Pageable pageable) {
        return jpaRepository.findByCustomerId(customerId, pageable).map(this::toDomain);
    }

    private SimulationEntity toEntity(SimulationResult r) {
        SimulationEntity e = new SimulationEntity();
        e.setId(r.getId());
        e.setCustomerId(r.getCustomerId());
        e.setScenarioType(r.getScenarioType());
        e.setCurrentScore(r.getCurrentScore());
        e.setEstimatedScore(r.getEstimatedScore());
        e.setPointImpact(r.getPointImpact());
        e.setConfidence(r.getConfidence());
        e.setFactorsChanged(r.getFactorsChanged() != null ? String.join(",", r.getFactorsChanged()) : null);
        e.setDisclaimer(r.getDisclaimer());
        e.setCreatedAt(r.getCreatedAt());
        return e;
    }

    private SimulationResult toDomain(SimulationEntity e) {
        SimulationResult r = new SimulationResult();
        r.setId(e.getId());
        r.setCustomerId(e.getCustomerId());
        r.setScenarioType(e.getScenarioType());
        r.setCurrentScore(e.getCurrentScore());
        r.setEstimatedScore(e.getEstimatedScore());
        r.setPointImpact(e.getPointImpact());
        r.setConfidence(e.getConfidence());
        r.setFactorsChanged(e.getFactorsChanged() != null ? Arrays.asList(e.getFactorsChanged().split(",")) : List.of());
        r.setDisclaimer(e.getDisclaimer());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
