package com.lloyds.simulationservice.infrastructure.client;

import com.lloyds.simulationservice.application.command.RunSimulationCommand;
import com.lloyds.simulationservice.domain.exception.SimulationTimeoutException;
import com.lloyds.simulationservice.domain.model.Confidence;
import com.lloyds.simulationservice.domain.model.SimulationResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class VertexAiSimulationClient {

    private static final Logger log = LoggerFactory.getLogger(VertexAiSimulationClient.class);
    private final RestClient restClient;

    public VertexAiSimulationClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("${VERTEX_AI_BASE_URL:http://localhost:8090}").build();
    }

    @CircuitBreaker(name = "vertexAiSimulation", fallbackMethod = "simulationFallback")
    @TimeLimiter(name = "vertexAiSimulation")
    public SimulationResult runSimulation(RunSimulationCommand command) {
        log.info("Calling Vertex AI simulation model for scenario: {}", command.scenarioType());
        return restClient.post()
                .uri("/v1/simulations/predict")
                .body(command)
                .retrieve()
                .body(SimulationResult.class);
    }

    private SimulationResult simulationFallback(RunSimulationCommand command, Throwable t) {
        log.warn("Vertex AI simulation unavailable, fallback triggered. Reason: {}", t.getMessage());
        throw new SimulationTimeoutException("Simulation taking too long. Please try again shortly.");
    }
}
