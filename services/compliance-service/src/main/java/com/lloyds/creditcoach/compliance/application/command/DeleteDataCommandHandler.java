package com.lloyds.creditcoach.compliance.application.command;

import com.lloyds.creditcoach.compliance.infrastructure.client.ServiceAggregatorClient;
import com.lloyds.creditcoach.compliance.infrastructure.messaging.ComplianceEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteDataCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteDataCommandHandler.class);
    private final ServiceAggregatorClient serviceAggregatorClient;
    private final ComplianceEventPublisher eventPublisher;

    public DeleteDataCommandHandler(ServiceAggregatorClient serviceAggregatorClient,
                                    ComplianceEventPublisher eventPublisher) {
        this.serviceAggregatorClient = serviceAggregatorClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(UUID customerId) {
        log.info("Processing GDPR Art.17 deletion for customer: {}", customerId);

        // Delete from all services — CCA-exempt records retained by each service
        serviceAggregatorClient.deleteConsentData(customerId);
        serviceAggregatorClient.deleteScoreData(customerId);
        serviceAggregatorClient.deletePlanData(customerId);
        serviceAggregatorClient.deleteOfferData(customerId);
        serviceAggregatorClient.deleteAlertData(customerId);

        eventPublisher.publishDataDeleted(customerId);
        log.info("GDPR deletion completed for customer: {} (CCA-exempt records retained)", customerId);
    }
}
