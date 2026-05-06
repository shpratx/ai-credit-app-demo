package com.lloyds.creditcoach.creditscore.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/credit-coach/admin")
public class AdminController {

    @GetMapping("/refresh-schedule")
    public ResponseEntity<Map<String, Object>> getRefreshConfig() {
        // In production: query ScoreRefreshScheduleRepository
        return ResponseEntity.ok(Map.of(
                "provider", "EXPERIAN",
                "frequencyDays", 30,
                "status", "ACTIVE"
        ));
    }
}
