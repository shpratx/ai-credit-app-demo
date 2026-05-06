package com.lloyds.offerservice.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * FCA Consumer Duty: checks for vulnerability markers.
 * Indicators: missed payments, gambling transactions, payday loan usage from CRA data.
 */
@Component
public class DistressIndicatorService {

    private static final Logger log = LoggerFactory.getLogger(DistressIndicatorService.class);
    private final RestClient restClient;

    public DistressIndicatorService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("${DISTRESS_SERVICE_URL:http://localhost:8091}").build();
    }

    public boolean isCustomerInDistress(UUID customerId) {
        log.info("Checking distress indicators for customer: {}", customerId);
        try {
            var result = restClient.get()
                    .uri("/api/v1/distress-indicators/{customerId}", customerId)
                    .retrieve()
                    .body(DistressCheckResponse.class);
            return result != null && result.distressed();
        } catch (Exception e) {
            log.warn("Distress check failed, defaulting to not distressed: {}", e.getMessage());
            return false;
        }
    }

    private record DistressCheckResponse(boolean distressed, String reason) {}
}
