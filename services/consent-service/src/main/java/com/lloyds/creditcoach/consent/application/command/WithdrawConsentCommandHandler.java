package com.lloyds.creditcoach.consent.application.command;

import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.domain.model.Consent;
import com.lloyds.creditcoach.consent.domain.port.ConsentRepository;
import com.lloyds.creditcoach.consent.infrastructure.messaging.ConsentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class WithdrawConsentCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(WithdrawConsentCommandHandler.class);

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public WithdrawConsentCommandHandler(ConsentRepository consentRepository, ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConsentResponse handle(UUID consentId, UUID customerId, String reason) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consent not found"));

        if (!consent.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (consent.isWithdrawn()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Consent already withdrawn");
        }

        Consent withdrawal = consent.withdraw();
        Consent saved = consentRepository.save(withdrawal);

        log.info("Consent withdrawn: consentId={}, customerId={}, reason={}", saved.getId(), customerId, reason);

        eventPublisher.publishConsentRevoked(saved);

        return new ConsentResponse(
                saved.getId(),
                saved.getCustomerId(),
                saved.getCraProvider(),
                saved.getStatus(),
                saved.getGrantedAt(),
                saved.getWithdrawnAt()
        );
    }
}
