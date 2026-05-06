package com.lloyds.creditcoach.creditscore.api.controller;

import com.lloyds.creditcoach.creditscore.application.command.RefreshScoreCommandHandler;
import com.lloyds.creditcoach.creditscore.application.dto.*;
import com.lloyds.creditcoach.creditscore.application.query.GetChangeExplanationQueryHandler;
import com.lloyds.creditcoach.creditscore.application.query.GetFactorsQueryHandler;
import com.lloyds.creditcoach.creditscore.application.query.GetScoreHistoryQueryHandler;
import com.lloyds.creditcoach.creditscore.application.query.GetScoreQueryHandler;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/scores")
public class ScoreController {

    private final GetScoreQueryHandler getScoreHandler;
    private final RefreshScoreCommandHandler refreshHandler;
    private final GetFactorsQueryHandler getFactorsHandler;
    private final GetChangeExplanationQueryHandler changeExplanationHandler;
    private final GetScoreHistoryQueryHandler historyHandler;

    public ScoreController(GetScoreQueryHandler getScoreHandler,
                           RefreshScoreCommandHandler refreshHandler,
                           GetFactorsQueryHandler getFactorsHandler,
                           GetChangeExplanationQueryHandler changeExplanationHandler,
                           GetScoreHistoryQueryHandler historyHandler) {
        this.getScoreHandler = getScoreHandler;
        this.refreshHandler = refreshHandler;
        this.getFactorsHandler = getFactorsHandler;
        this.changeExplanationHandler = changeExplanationHandler;
        this.historyHandler = historyHandler;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ScoreResponse> getCurrentScore(@PathVariable UUID customerId) {
        return ResponseEntity.ok(getScoreHandler.handle(customerId));
    }

    @PostMapping("/{customerId}/refresh")
    public ResponseEntity<ScoreRetrievingResponse> refreshScore(
            @PathVariable UUID customerId,
            @RequestHeader("X-Correlation-Id") UUID correlationId) {
        return ResponseEntity.accepted().body(refreshHandler.handleRefresh(customerId, correlationId));
    }

    @GetMapping("/{customerId}/factors")
    public ResponseEntity<FactorsResponse> getFactors(@PathVariable UUID customerId) {
        return ResponseEntity.ok(getFactorsHandler.handle(customerId));
    }

    @GetMapping("/{customerId}/change-explanation")
    public ResponseEntity<ChangeExplanationResponse> getChangeExplanation(@PathVariable UUID customerId) {
        return changeExplanationHandler.handle(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<ScoreHistoryResponse> getHistory(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "12") @Min(1) @Max(24) int months) {
        return ResponseEntity.ok(historyHandler.handle(customerId, months));
    }
}
