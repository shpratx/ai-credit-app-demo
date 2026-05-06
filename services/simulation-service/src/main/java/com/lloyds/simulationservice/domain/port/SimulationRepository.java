package com.lloyds.simulationservice.domain.port;

import com.lloyds.simulationservice.domain.model.SimulationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SimulationRepository {
    SimulationResult save(SimulationResult result);
    Page<SimulationResult> findByCustomerId(UUID customerId, Pageable pageable);
}
