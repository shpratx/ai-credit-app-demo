package com.lloyds.creditcoach.consent.application.query;

import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.application.dto.ConsentsListResponse;
import com.lloyds.creditcoach.consent.domain.model.Consent;
import com.lloyds.creditcoach.consent.domain.port.ConsentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetConsentsQueryHandler {

    private final ConsentRepository consentRepository;

    public GetConsentsQueryHandler(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Transactional(readOnly = true)
    public ConsentsListResponse handle(UUID customerId) {
        var consents = consentRepository.findLatestByCustomer(customerId).stream()
                .map(this::toResponse)
                .toList();
        return new ConsentsListResponse(consents);
    }

    private ConsentResponse toResponse(Consent consent) {
        return new ConsentResponse(
                consent.getId(),
                consent.getCustomerId(),
                consent.getCraProvider(),
                consent.getStatus(),
                consent.getGrantedAt(),
                consent.getWithdrawnAt()
        );
    }
}
