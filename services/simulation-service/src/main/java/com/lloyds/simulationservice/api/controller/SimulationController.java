package com.lloyds.simulationservice.api.controller;

import com.lloyds.simulationservice.api.dto.ApiResponse;
import com.lloyds.simulationservice.application.command.RunSimulationCommand;
import com.lloyds.simulationservice.application.command.RunSimulationCommandHandler;
import com.lloyds.simulationservice.application.dto.SimulationResponse;
import com.lloyds.simulationservice.application.query.GetSimulationHistoryQuery;
import com.lloyds.simulationservice.application.query.GetSimulationHistoryQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/simulations")
@Tag(name = "Simulations", description = "Credit score simulation endpoints")
public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);

    private final RunSimulationCommandHandler runHandler;
    private final GetSimulationHistoryQueryHandler historyHandler;

    public SimulationController(RunSimulationCommandHandler runHandler, GetSimulationHistoryQueryHandler historyHandler) {
        this.runHandler = runHandler;
        this.historyHandler = historyHandler;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Run a credit score simulation scenario")
    public ResponseEntity<ApiResponse<SimulationResponse>> runSimulation(@Valid @RequestBody RunSimulationCommand command) {
        log.info("POST /api/v1/credit-coach/simulations");
        var result = runHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(result));
    }

    @GetMapping("/{customerId}/history")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get simulation history for a customer")
    public ResponseEntity<ApiResponse<java.util.List<SimulationResponse>>> getHistory(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/credit-coach/simulations/{}/history", customerId);
        Page<SimulationResponse> result = historyHandler.handle(new GetSimulationHistoryQuery(customerId, page, size));
        return ResponseEntity.ok(ApiResponse.paginated(result.getContent(), page, size, result.getTotalElements(), result.getTotalPages()));
    }
}
