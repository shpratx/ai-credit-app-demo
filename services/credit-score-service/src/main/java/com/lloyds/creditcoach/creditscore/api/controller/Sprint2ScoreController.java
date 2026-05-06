package com.lloyds.creditcoach.creditscore.api.controller;

import com.lloyds.creditcoach.creditscore.application.dto.DebtOverviewResponse;
import com.lloyds.creditcoach.creditscore.application.dto.MultiBureauResponse;
import com.lloyds.creditcoach.creditscore.application.dto.ScoreTrendResponse;
import com.lloyds.creditcoach.creditscore.application.query.GetDebtOverviewQueryHandler;
import com.lloyds.creditcoach.creditscore.application.query.GetMultiBureauComparisonQueryHandler;
import com.lloyds.creditcoach.creditscore.application.query.GetScoreTrendQueryHandler;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach")
public class Sprint2ScoreController {

    private static final Logger log = LoggerFactory.getLogger(Sprint2ScoreController.class);

    private final GetScoreTrendQueryHandler scoreTrendHandler;
    private final GetDebtOverviewQueryHandler debtOverviewHandler;
    private final GetMultiBureauComparisonQueryHandler multiBureauHandler;

    public Sprint2ScoreController(GetScoreTrendQueryHandler scoreTrendHandler,
                                  GetDebtOverviewQueryHandler debtOverviewHandler,
                                  GetMultiBureauComparisonQueryHandler multiBureauHandler) {
        this.scoreTrendHandler = scoreTrendHandler;
        this.debtOverviewHandler = debtOverviewHandler;
        this.multiBureauHandler = multiBureauHandler;
    }

    @GetMapping("/score-history/{customerId}/trend")
    public ResponseEntity<ScoreTrendResponse> getScoreTrend(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "12") @Min(1) @Max(24) int months) {
        log.info("GET /score-history/{}/trend?months={}", customerId, months);
        return ResponseEntity.ok(scoreTrendHandler.handle(customerId, months));
    }

    @GetMapping("/debt-overview/{customerId}")
    public ResponseEntity<DebtOverviewResponse> getDebtOverview(@PathVariable UUID customerId) {
        log.info("GET /debt-overview/{}", customerId);
        return ResponseEntity.ok(debtOverviewHandler.handle(customerId));
    }

    @GetMapping("/scores/{customerId}/compare")
    public ResponseEntity<MultiBureauResponse> getMultiBureauComparison(@PathVariable UUID customerId) {
        log.info("GET /scores/{}/compare", customerId);
        return ResponseEntity.ok(multiBureauHandler.handle(customerId));
    }
}
