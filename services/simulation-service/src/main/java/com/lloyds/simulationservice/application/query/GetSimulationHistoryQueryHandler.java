package com.lloyds.simulationservice.application.query;

import com.lloyds.simulationservice.application.dto.SimulationResponse;
import com.lloyds.simulationservice.domain.model.SimulationResult;
import com.lloyds.simulationservice.domain.port.SimulationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSimulationHistoryQueryHandler implements QueryHandler<GetSimulationHistoryQuery, Page<SimulationResponse>> {

    private static final Logger log = LoggerFactory.getLogger(GetSimulationHistoryQueryHandler.class);
    private final SimulationRepository repository;

    public GetSimulationHistoryQueryHandler(SimulationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SimulationResponse> handle(GetSimulationHistoryQuery query) {
        log.info("Fetching simulation history for customer: {}", query.customerId());
        var pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByCustomerId(query.customerId(), pageable).map(this::toResponse);
    }

    private SimulationResponse toResponse(SimulationResult r) {
        return new SimulationResponse(
                r.getId(), r.getCustomerId(), r.getScenarioType(),
                r.getCurrentScore(), r.getEstimatedScore(), r.getPointImpact(),
                r.getConfidence(), r.getFactorsChanged(), r.getDisclaimer(), r.getCreatedAt());
    }
}
