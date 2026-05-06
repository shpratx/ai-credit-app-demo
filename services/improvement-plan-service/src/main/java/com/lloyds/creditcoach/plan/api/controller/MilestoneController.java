package com.lloyds.creditcoach.plan.api.controller;

import com.lloyds.creditcoach.plan.application.dto.MilestoneResponse;
import com.lloyds.creditcoach.plan.application.query.GetMilestonesQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/milestones")
public class MilestoneController {

    private static final Logger log = LoggerFactory.getLogger(MilestoneController.class);

    private final GetMilestonesQueryHandler milestonesHandler;

    public MilestoneController(GetMilestonesQueryHandler milestonesHandler) {
        this.milestonesHandler = milestonesHandler;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<MilestoneResponse>> getMilestones(@PathVariable UUID customerId) {
        log.info("GET /milestones/{}", customerId);
        return ResponseEntity.ok(milestonesHandler.handle(customerId));
    }
}
