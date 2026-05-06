package com.lloyds.creditcoach.alert.api.controller;

import com.lloyds.creditcoach.alert.application.command.DismissAlertCommandHandler;
import com.lloyds.creditcoach.alert.application.command.UpdatePreferencesCommandHandler;
import com.lloyds.creditcoach.alert.application.dto.AlertPreferenceResponse;
import com.lloyds.creditcoach.alert.application.dto.AlertResponse;
import com.lloyds.creditcoach.alert.application.dto.UpdatePreferencesRequest;
import com.lloyds.creditcoach.alert.application.query.GetAlertsQueryHandler;
import com.lloyds.creditcoach.alert.application.query.GetPreferencesQueryHandler;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);
    private final GetAlertsQueryHandler getAlertsHandler;
    private final GetPreferencesQueryHandler getPreferencesHandler;
    private final UpdatePreferencesCommandHandler updatePreferencesHandler;
    private final DismissAlertCommandHandler dismissAlertHandler;

    public AlertController(GetAlertsQueryHandler getAlertsHandler,
                           GetPreferencesQueryHandler getPreferencesHandler,
                           UpdatePreferencesCommandHandler updatePreferencesHandler,
                           DismissAlertCommandHandler dismissAlertHandler) {
        this.getAlertsHandler = getAlertsHandler;
        this.getPreferencesHandler = getPreferencesHandler;
        this.updatePreferencesHandler = updatePreferencesHandler;
        this.dismissAlertHandler = dismissAlertHandler;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Page<AlertResponse>> getAlerts(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /alerts/{}", customerId);
        return ResponseEntity.ok(getAlertsHandler.handle(customerId, page, size));
    }

    @GetMapping("/{customerId}/preferences")
    public ResponseEntity<AlertPreferenceResponse> getPreferences(@PathVariable UUID customerId) {
        log.info("GET /alerts/{}/preferences", customerId);
        return ResponseEntity.ok(getPreferencesHandler.handle(customerId));
    }

    @PutMapping("/{customerId}/preferences")
    public ResponseEntity<AlertPreferenceResponse> updatePreferences(
            @PathVariable UUID customerId,
            @Valid @RequestBody UpdatePreferencesRequest request) {
        log.info("PUT /alerts/{}/preferences", customerId);
        return ResponseEntity.ok(updatePreferencesHandler.handle(customerId, request));
    }

    @PostMapping("/{customerId}/dismiss/{alertId}")
    public ResponseEntity<Void> dismissAlert(@PathVariable UUID customerId, @PathVariable UUID alertId) {
        log.info("POST /alerts/{}/dismiss/{}", customerId, alertId);
        dismissAlertHandler.handle(customerId, alertId);
        return ResponseEntity.noContent().build();
    }
}
