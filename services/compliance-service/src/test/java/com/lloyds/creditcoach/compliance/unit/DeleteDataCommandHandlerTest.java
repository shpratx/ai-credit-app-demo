package com.lloyds.creditcoach.compliance.unit;

import com.lloyds.creditcoach.compliance.application.command.DeleteDataCommandHandler;
import com.lloyds.creditcoach.compliance.infrastructure.client.ServiceAggregatorClient;
import com.lloyds.creditcoach.compliance.infrastructure.messaging.ComplianceEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteDataCommandHandler")
class DeleteDataCommandHandlerTest {

    @Mock private ServiceAggregatorClient serviceAggregatorClient;
    @Mock private ComplianceEventPublisher eventPublisher;

    private DeleteDataCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DeleteDataCommandHandler(serviceAggregatorClient, eventPublisher);
    }

    @Test
    @DisplayName("should delete data from all services and retain CCA-exempt records")
    void should_deleteFromAllServices_when_gdprDeletionRequested() {
        var customerId = UUID.randomUUID();

        handler.handle(customerId);

        verify(serviceAggregatorClient).deleteConsentData(customerId);
        verify(serviceAggregatorClient).deleteScoreData(customerId);
        verify(serviceAggregatorClient).deletePlanData(customerId);
        verify(serviceAggregatorClient).deleteOfferData(customerId);
        verify(serviceAggregatorClient).deleteAlertData(customerId);
        verify(eventPublisher).publishDataDeleted(customerId);
    }
}
