package com.lloyds.creditcoach.consent.api.controller;

import com.lloyds.creditcoach.consent.application.command.GrantConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.command.WithdrawConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.dto.*;
import com.lloyds.creditcoach.consent.application.query.GetConsentsQueryHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/consents")
public class ConsentController {

    private final GrantConsentCommandHandler grantHandler;
    private final WithdrawConsentCommandHandler withdrawHandler;
    private final GetConsentsQueryHandler getConsentsHandler;

    public ConsentController(GrantConsentCommandHandler grantHandler,
                             WithdrawConsentCommandHandler withdrawHandler,
                             GetConsentsQueryHandler getConsentsHandler) {
        this.grantHandler = grantHandler;
        this.withdrawHandler = withdrawHandler;
        this.getConsentsHandler = getConsentsHandler;
    }

    @PostMapping
    public ResponseEntity<ConsentResponse> grantConsent(
            @Valid @RequestBody GrantConsentRequest request,
            @RequestHeader("X-Customer-Id") UUID customerId,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String deviceFingerprint = httpRequest.getHeader("X-Device-Fingerprint");

        ConsentResponse response = grantHandler.handle(request, customerId, ipAddress, deviceFingerprint);
        return ResponseEntity.created(URI.create("/api/v1/credit-coach/consents/" + response.consentId()))
                .body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ConsentsListResponse> getConsents(@PathVariable UUID customerId) {
        return ResponseEntity.ok(getConsentsHandler.handle(customerId));
    }

    @PostMapping("/{consentId}/withdraw")
    public ResponseEntity<ConsentResponse> withdrawConsent(
            @PathVariable UUID consentId,
            @RequestHeader("X-Customer-Id") UUID customerId,
            @Valid @RequestBody(required = false) WithdrawConsentRequest request) {

        String reason = request != null ? request.reason() : null;
        ConsentResponse response = withdrawHandler.handle(consentId, customerId, reason);
        return ResponseEntity.ok(response);
    }
}
