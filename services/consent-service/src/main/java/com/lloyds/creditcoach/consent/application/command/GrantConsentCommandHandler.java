package com.lloyds.creditcoach.consent.application.command;

import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.application.dto.GrantConsentRequest;
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
public class GrantConsentCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(GrantConsentCommandHandler.class);

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public GrantConsentCommandHandler(ConsentRepository consentRepository, ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConsentResponse handle(GrantConsentRequest request, UUID customerId, String ipAddress, String deviceFingerprint) {
        // Check no active consent exists for this CRA
        consentRepository.findActiveByCustomerAndProvider(customerId, request.craProvider())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Active consent already exists for " + request.craProvider());
                });

        // Create consent
        Consent consent = Consent.grant(
                customerId,
                request.craProvider(),
                request.consentTextVersion(),
                request.consentTextHash(),
                request.channel(),
                ipAddress,
                deviceFingerprint
        );

        Consent saved = consentRepository.save(consent);

        log.info("Consent granted: consentId={}, customerId={}, provider={}", saved.getId(), customerId, request.craProvider());

        // Publish event
        eventPublisher.publishConsentGranted(saved);

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
