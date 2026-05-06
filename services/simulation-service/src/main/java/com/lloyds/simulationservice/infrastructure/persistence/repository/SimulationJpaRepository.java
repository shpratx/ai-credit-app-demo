package com.lloyds.simulationservice.infrastructure.persistence.repository;

import com.lloyds.simulationservice.infrastructure.persistence.entity.SimulationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SimulationJpaRepository extends JpaRepository<SimulationEntity, UUID> {
    Page<SimulationEntity> findByCustomerId(UUID customerId, Pageable pageable);
}
