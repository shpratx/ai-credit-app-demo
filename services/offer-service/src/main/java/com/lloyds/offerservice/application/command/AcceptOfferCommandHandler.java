package com.lloyds.offerservice.application.command;

import com.lloyds.offerservice.application.dto.AcceptOfferResponse;
import com.lloyds.offerservice.domain.exception.BusinessRuleException;
import com.lloyds.offerservice.domain.exception.EntityNotFoundException;
import com.lloyds.offerservice.domain.model.AuditAction;
import com.lloyds.offerservice.domain.model.OfferAuditEntry;
import com.lloyds.offerservice.domain.model.OfferStatus;
import com.lloyds.offerservice.domain.port.OfferAuditRepository;
import com.lloyds.offerservice.domain.port.OfferRepository;
import com.lloyds.offerservice.infrastructure.messaging.OfferEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AcceptOfferCommandHandler implements CommandHandler<AcceptOfferCommand, AcceptOfferResponse> {

    private static final Logger log = LoggerFactory.getLogger(AcceptOfferCommandHandler.class);
    private static final int COOLING_OFF_DAYS = 14; // CCA s.66A
    private static final BigDecimal LATE_PAYMENT_FEE_CAP = new BigDecimal("12.00"); // CONC 7.7.5R
    private static final BigDecimal EARLY_REPAYMENT_FEE = BigDecimal.ZERO; // CCA s.95A (Lloyds 0%)

    private final OfferRepository offerRepository;
    private final OfferAuditRepository auditRepository;
    private final OfferEventPublisher eventPublisher;

    public AcceptOfferCommandHandler(OfferRepository offerRepository, OfferAuditRepository auditRepository, OfferEventPublisher eventPublisher) {
        this.offerRepository = offerRepository;
        this.auditRepository = auditRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AcceptOfferResponse handle(AcceptOfferCommand command) {
        log.info("Accepting offer: {}", command.offerId());

        var offer = offerRepository.findById(command.offerId())
                .orElseThrow(() -> new EntityNotFoundException("PreApprovedOffer", command.offerId()));

        if (!offer.isValid()) {
            throw new BusinessRuleException("OFFER_EXPIRED", "This offer is no longer available");
        }

        offer.setStatus(OfferStatus.ACCEPTED);
        offerRepository.save(offer);

        auditRepository.save(OfferAuditEntry.create(offer.getId(), command.customerId(), AuditAction.ACCEPTED, null));
        eventPublisher.publishOfferAccepted(offer.getId(), command.customerId());

        Instant now = Instant.now();
        return new AcceptOfferResponse(
                offer.getId(),
                "ACCEPTED",
                now,
                String.format("You have %d days to withdraw from this agreement without giving a reason (CCA s.66A).", COOLING_OFF_DAYS),
                now.plus(COOLING_OFF_DAYS, ChronoUnit.DAYS),
                EARLY_REPAYMENT_FEE,
                LATE_PAYMENT_FEE_CAP);
    }
}
