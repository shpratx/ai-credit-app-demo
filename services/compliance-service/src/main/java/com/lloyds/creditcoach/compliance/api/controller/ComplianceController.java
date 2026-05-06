package com.lloyds.creditcoach.compliance.api.controller;

import com.lloyds.creditcoach.compliance.application.command.ActivateBreathingSpaceCommandHandler;
import com.lloyds.creditcoach.compliance.application.command.DeleteDataCommandHandler;
import com.lloyds.creditcoach.compliance.application.dto.*;
import com.lloyds.creditcoach.compliance.application.query.CheckVulnerabilityQueryHandler;
import com.lloyds.creditcoach.compliance.application.query.ExportDsarQueryHandler;
import com.lloyds.creditcoach.compliance.application.query.GetDecisionExplanationQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/compliance")
public class ComplianceController {

    private static final Logger log = LoggerFactory.getLogger(ComplianceController.class);
    private final DeleteDataCommandHandler deleteDataHandler;
    private final ExportDsarQueryHandler exportDsarHandler;
    private final GetDecisionExplanationQueryHandler decisionExplanationHandler;
    private final ActivateBreathingSpaceCommandHandler breathingSpaceHandler;
    private final CheckVulnerabilityQueryHandler vulnerabilityHandler;

    public ComplianceController(DeleteDataCommandHandler deleteDataHandler,
                                ExportDsarQueryHandler exportDsarHandler,
                                GetDecisionExplanationQueryHandler decisionExplanationHandler,
                                ActivateBreathingSpaceCommandHandler breathingSpaceHandler,
                                CheckVulnerabilityQueryHandler vulnerabilityHandler) {
        this.deleteDataHandler = deleteDataHandler;
        this.exportDsarHandler = exportDsarHandler;
        this.decisionExplanationHandler = decisionExplanationHandler;
        this.breathingSpaceHandler = breathingSpaceHandler;
        this.vulnerabilityHandler = vulnerabilityHandler;
    }

    @PostMapping("/{customerId}/delete")
    public ResponseEntity<Void> deleteData(@PathVariable UUID customerId) {
        log.info("POST /compliance/{}/delete", customerId);
        deleteDataHandler.handle(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/dsar")
    public ResponseEntity<DsarExportResponse> exportDsar(@PathVariable UUID customerId) {
        log.info("GET /compliance/{}/dsar", customerId);
        return ResponseEntity.ok(exportDsarHandler.handle(customerId));
    }

    @GetMapping("/{customerId}/decision-explanation/{offerId}")
    public ResponseEntity<DecisionExplanationResponse> getDecisionExplanation(
            @PathVariable UUID customerId, @PathVariable UUID offerId) {
        log.info("GET /compliance/{}/decision-explanation/{}", customerId, offerId);
        return ResponseEntity.ok(decisionExplanationHandler.handle(customerId, offerId));
    }

    @PostMapping("/{customerId}/breathing-space")
    public ResponseEntity<BreathingSpaceResponse> activateBreathingSpace(@PathVariable UUID customerId) {
        log.info("POST /compliance/{}/breathing-space", customerId);
        return ResponseEntity.ok(breathingSpaceHandler.handle(customerId));
    }

    @GetMapping("/{customerId}/vulnerability-check")
    public ResponseEntity<VulnerabilityCheckResponse> checkVulnerability(@PathVariable UUID customerId) {
        log.info("GET /compliance/{}/vulnerability-check", customerId);
        return ResponseEntity.ok(vulnerabilityHandler.handle(customerId));
    }
}
