package com.lloyds.offerservice.infrastructure.client;

import com.lloyds.offerservice.domain.model.PreApprovedOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
public class EligibilityMatchingClient {

    private static final Logger log = LoggerFactory.getLogger(EligibilityMatchingClient.class);
    private final RestClient restClient;

    public EligibilityMatchingClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("${ELIGIBILITY_SERVICE_URL:http://localhost:8092}").build();
    }

    public List<PreApprovedOffer> getEligibleOffers(UUID customerId) {
        log.info("Fetching eligible offers for customer: {}", customerId);
        return restClient.get()
                .uri("/api/v1/eligibility/{customerId}/offers", customerId)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }
}
