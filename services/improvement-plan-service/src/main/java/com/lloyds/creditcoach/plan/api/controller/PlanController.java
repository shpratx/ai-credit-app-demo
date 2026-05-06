package com.lloyds.creditcoach.plan.api.controller;

import com.lloyds.creditcoach.plan.application.command.CompleteActionCommandHandler;
import com.lloyds.creditcoach.plan.application.command.RefreshPlanCommandHandler;
import com.lloyds.creditcoach.plan.application.dto.ActionResponse;
import com.lloyds.creditcoach.plan.application.dto.PlanResponse;
import com.lloyds.creditcoach.plan.application.dto.SpendingImpactResponse;
import com.lloyds.creditcoach.plan.application.query.GetActionHistoryQueryHandler;
import com.lloyds.creditcoach.plan.application.query.GetPlanQueryHandler;
import com.lloyds.creditcoach.plan.application.query.GetSpendingImpactQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/improvement-plans")
public class PlanController {

    private static final Logger log = LoggerFactory.getLogger(PlanController.class);

    private final GetPlanQueryHandler getPlanHandler;
    private final RefreshPlanCommandHandler refreshPlanHandler;
    private final CompleteActionCommandHandler completeActionHandler;
    private final GetActionHistoryQueryHandler actionHistoryHandler;
    private final GetSpendingImpactQueryHandler spendingImpactHandler;

    public PlanController(GetPlanQueryHandler getPlanHandler,
                          RefreshPlanCommandHandler refreshPlanHandler,
                          CompleteActionCommandHandler completeActionHandler,
                          GetActionHistoryQueryHandler actionHistoryHandler,
                          GetSpendingImpactQueryHandler spendingImpactHandler) {
        this.getPlanHandler = getPlanHandler;
        this.refreshPlanHandler = refreshPlanHandler;
        this.completeActionHandler = completeActionHandler;
        this.actionHistoryHandler = actionHistoryHandler;
        this.spendingImpactHandler = spendingImpactHandler;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable UUID customerId) {
        log.info("GET /improvement-plans/{}", customerId);
        return ResponseEntity.ok(getPlanHandler.handle(customerId));
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<PlanResponse> refreshPlan(@PathVariable UUID customerId) {
        log.info("POST /improvement-plans/{}", customerId);
        return ResponseEntity.ok(refreshPlanHandler.handle(customerId));
    }

    @PostMapping("/{customerId}/actions/{actionId}/complete")
    public ResponseEntity<ActionResponse> completeAction(
            @PathVariable UUID customerId,
            @PathVariable UUID actionId) {
        log.info("POST /improvement-plans/{}/actions/{}/complete", customerId, actionId);
        return ResponseEntity.ok(completeActionHandler.handle(customerId, actionId));
    }

    @GetMapping("/{customerId}/actions")
    public ResponseEntity<List<ActionResponse>> getActions(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "all") String status) {
        log.info("GET /improvement-plans/{}/actions?status={}", customerId, status);
        return ResponseEntity.ok(actionHistoryHandler.handle(customerId, status));
    }

    @GetMapping("/{customerId}/spending-impact")
    public ResponseEntity<SpendingImpactResponse> getSpendingImpact(@PathVariable UUID customerId) {
        log.info("GET /spending-impact/{}", customerId);
        return ResponseEntity.ok(spendingImpactHandler.handle(customerId));
    }
}
