package com.lloyds.creditcoach.compliance.application.query;

import com.lloyds.creditcoach.compliance.application.dto.DecisionExplanationResponse;
import com.lloyds.creditcoach.compliance.infrastructure.client.ServiceAggregatorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetDecisionExplanationQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetDecisionExplanationQueryHandler.class);
    private final ServiceAggregatorClient serviceAggregatorClient;

    public GetDecisionExplanationQueryHandler(ServiceAggregatorClient serviceAggregatorClient) {
        this.serviceAggregatorClient = serviceAggregatorClient;
    }

    @Transactional(readOnly = true)
    public DecisionExplanationResponse handle(UUID customerId, UUID offerId) {
        log.info("Fetching decision explanation for customer: {}, offer: {}", customerId, offerId);
        // GDPR Art. 22 — ADM explanation with human review option
        return serviceAggregatorClient.getOfferDecisionExplanation(customerId, offerId);
    }
}
