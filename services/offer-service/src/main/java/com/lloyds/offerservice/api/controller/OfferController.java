package com.lloyds.offerservice.api.controller;

import com.lloyds.offerservice.api.dto.ApiResponse;
import com.lloyds.offerservice.application.command.AcceptOfferCommand;
import com.lloyds.offerservice.application.command.AcceptOfferCommandHandler;
import com.lloyds.offerservice.application.dto.AcceptOfferResponse;
import com.lloyds.offerservice.application.dto.OfferResponse;
import com.lloyds.offerservice.application.dto.SecciDocument;
import com.lloyds.offerservice.application.query.*;
import com.lloyds.offerservice.domain.model.OfferAuditEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-coach/offers")
@Tag(name = "Pre-Approved Offers", description = "Pre-approved credit offer endpoints")
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);

    private final GetOffersQueryHandler getOffersHandler;
    private final AcceptOfferCommandHandler acceptHandler;
    private final GetSecciQueryHandler secciHandler;
    private final GetOfferAuditQueryHandler auditHandler;

    public OfferController(GetOffersQueryHandler getOffersHandler,
                           AcceptOfferCommandHandler acceptHandler,
                           GetSecciQueryHandler secciHandler,
                           GetOfferAuditQueryHandler auditHandler) {
        this.getOffersHandler = getOffersHandler;
        this.acceptHandler = acceptHandler;
        this.secciHandler = secciHandler;
        this.auditHandler = auditHandler;
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get pre-approved offers for a customer")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getOffers(@PathVariable UUID customerId) {
        log.info("GET /api/v1/credit-coach/offers/{}", customerId);
        var result = getOffersHandler.handle(new GetOffersQuery(customerId));
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @PostMapping("/{offerId}/accept")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Accept a pre-approved offer (one-tap)")
    public ResponseEntity<ApiResponse<AcceptOfferResponse>> acceptOffer(
            @PathVariable UUID offerId, @AuthenticationPrincipal Jwt jwt) {
        log.info("POST /api/v1/credit-coach/offers/{}/accept", offerId);
        UUID customerId = UUID.fromString(jwt.getSubject());
        var result = acceptHandler.handle(new AcceptOfferCommand(offerId, customerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(result));
    }

    @GetMapping("/{offerId}/secci")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get SECCI document for an offer (CCA s.55A)")
    public ResponseEntity<ApiResponse<SecciDocument>> getSecci(@PathVariable UUID offerId) {
        log.info("GET /api/v1/credit-coach/offers/{}/secci", offerId);
        var result = secciHandler.handle(new GetSecciQuery(offerId));
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @GetMapping("/{customerId}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get offer audit trail for compliance")
    public ResponseEntity<ApiResponse<List<OfferAuditEntry>>> getAudit(@PathVariable UUID customerId) {
        log.info("GET /api/v1/credit-coach/offers/{}/audit", customerId);
        var result = auditHandler.handle(new GetOfferAuditQuery(customerId));
        return ResponseEntity.ok(ApiResponse.of(result));
    }
}
