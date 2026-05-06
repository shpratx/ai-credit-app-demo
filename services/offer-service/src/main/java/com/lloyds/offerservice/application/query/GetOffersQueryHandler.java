package com.lloyds.offerservice.application.query;

import com.lloyds.offerservice.application.dto.OfferResponse;
import com.lloyds.offerservice.domain.model.AuditAction;
import com.lloyds.offerservice.domain.model.OfferAuditEntry;
import com.lloyds.offerservice.domain.model.OfferStatus;
import com.lloyds.offerservice.domain.model.PreApprovedOffer;
import com.lloyds.offerservice.domain.port.OfferAuditRepository;
import com.lloyds.offerservice.domain.port.OfferRepository;
import com.lloyds.offerservice.infrastructure.client.DistressIndicatorService;
import com.lloyds.offerservice.infrastructure.client.EligibilityMatchingClient;
import com.lloyds.offerservice.infrastructure.messaging.OfferEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetOffersQueryHandler implements QueryHandler<GetOffersQuery, List<OfferResponse>> {

    private static final Logger log = LoggerFactory.getLogger(GetOffersQueryHandler.class);

    private final DistressIndicatorService distressService;
    private final EligibilityMatchingClient eligibilityClient;
    private final OfferRepository offerRepository;
    private final OfferAuditRepository auditRepository;
    private final OfferEventPublisher eventPublisher;

    public GetOffersQueryHandler(DistressIndicatorService distressService,
                                 EligibilityMatchingClient eligibilityClient,
                                 OfferRepository offerRepository,
                                 OfferAuditRepository auditRepository,
                                 OfferEventPublisher eventPublisher) {
        this.distressService = distressService;
        this.eligibilityClient = eligibilityClient;
        this.offerRepository = offerRepository;
        this.auditRepository = auditRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> handle(GetOffersQuery query) {
        log.info("Fetching offers for customer: {}", query.customerId());

        // FR-37 / FCA Consumer Duty: suppress offers during financial distress
        if (distressService.isCustomerInDistress(query.customerId())) {
            log.info("Customer {} in financial distress — suppressing offers", query.customerId());
            return List.of();
        }

        List<PreApprovedOffer> offers = offerRepository.findAvailableByCustomerId(query.customerId());
        if (offers.isEmpty()) {
            offers = eligibilityClient.getEligibleOffers(query.customerId());
            offers.forEach(offerRepository::save);
        }

        // Audit and publish
        offers.forEach(offer -> {
            auditRepository.save(OfferAuditEntry.create(offer.getId(), query.customerId(), AuditAction.PRESENTED, null));
            eventPublisher.publishOfferPresented(offer.getId(), query.customerId());
        });

        return offers.stream().map(this::toResponse).toList();
    }

    private OfferResponse toResponse(PreApprovedOffer o) {
        // CONC 3.5.5R: representative example with all 6 values
        var example = new OfferResponse.RepresentativeExample(
                o.getAmount(), o.getRate(), o.getApr(), o.getTerm(), o.getMonthlyPayment(), o.getTotalPayable());
        return new OfferResponse(o.getId(), o.getCustomerId(), o.getProductId(),
                o.getAmount(), o.getRate(), o.getApr(), o.getTerm(),
                o.getMonthlyPayment(), o.getTotalPayable(), o.getTotalChargeForCredit(),
                o.getStatus(), o.getValidUntil(), o.getCreatedAt(), example);
    }
}
