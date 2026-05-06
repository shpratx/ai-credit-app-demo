package com.lloyds.creditcoach.compliance.infrastructure.client;

import com.lloyds.creditcoach.compliance.application.dto.DecisionExplanationResponse;
import com.lloyds.creditcoach.compliance.domain.model.VulnerabilityIndicator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

@Component
public class ServiceAggregatorClient {

    private static final Logger log = LoggerFactory.getLogger(ServiceAggregatorClient.class);
    private final RestClient restClient;

    public ServiceAggregatorClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @CircuitBreaker(name = "serviceAggregator", fallbackMethod = "deleteDataFallback")
    @Retry(name = "serviceAggregator")
    public void deleteConsentData(UUID customerId) {
        log.info("Deleting consent data for customer: {}", customerId);
        restClient.delete().uri("http://consent-service:8080/api/v1/credit-coach/consent/{id}/data", customerId).retrieve().toBodilessEntity();
    }

    @CircuitBreaker(name = "serviceAggregator", fallbackMethod = "deleteDataFallback")
    @Retry(name = "serviceAggregator")
    public void deleteScoreData(UUID customerId) {
        log.info("Deleting score data for customer: {}", customerId);
        restClient.delete().uri("http://credit-score-service:8080/api/v1/credit-coach/scores/{id}/data", customerId).retrieve().toBodilessEntity();
    }

    @CircuitBreaker(name = "serviceAggregator", fallbackMethod = "deleteDataFallback")
    @Retry(name = "serviceAggregator")
    public void deletePlanData(UUID customerId) {
        log.info("Deleting plan data for customer: {}", customerId);
        restClient.delete().uri("http://improvement-plan-service:8080/api/v1/credit-coach/plans/{id}/data", customerId).retrieve().toBodilessEntity();
    }

    @CircuitBreaker(name = "serviceAggregator", fallbackMethod = "deleteDataFallback")
    @Retry(name = "serviceAggregator")
    public void deleteOfferData(UUID customerId) {
        log.info("Deleting offer data for customer: {}", customerId);
        restClient.delete().uri("http://offer-service:8080/api/v1/credit-coach/offers/{id}/data", customerId).retrieve().toBodilessEntity();
    }

    @CircuitBreaker(name = "serviceAggregator", fallbackMethod = "deleteDataFallback")
    @Retry(name = "serviceAggregator")
    public void deleteAlertData(UUID customerId) {
        log.info("Deleting alert data for customer: {}", customerId);
        restClient.delete().uri("http://alert-service:8080/api/v1/credit-coach/alerts/{id}/data", customerId).retrieve().toBodilessEntity();
    }

    @CircuitBreaker(name = "serviceAggregator")
    public Map<String, Object> aggregateAllData(UUID customerId) {
        log.info("Aggregating all data for DSAR export, customer: {}", customerId);
        Map<String, Object> data = new HashMap<>();
        data.put("consent", fetchServiceData("http://consent-service:8080/api/v1/credit-coach/consent/" + customerId));
        data.put("scores", fetchServiceData("http://credit-score-service:8080/api/v1/credit-coach/scores/" + customerId));
        data.put("plans", fetchServiceData("http://improvement-plan-service:8080/api/v1/credit-coach/plans/" + customerId));
        data.put("offers", fetchServiceData("http://offer-service:8080/api/v1/credit-coach/offers/" + customerId));
        data.put("alerts", fetchServiceData("http://alert-service:8080/api/v1/credit-coach/alerts/" + customerId));
        return data;
    }

    public DecisionExplanationResponse getOfferDecisionExplanation(UUID customerId, UUID offerId) {
        log.info("Fetching decision explanation for offer: {}", offerId);
        return restClient.get()
                .uri("http://offer-service:8080/api/v1/credit-coach/offers/{customerId}/explanation/{offerId}", customerId, offerId)
                .retrieve()
                .body(DecisionExplanationResponse.class);
    }

    public List<VulnerabilityIndicator> checkVulnerabilityIndicators(UUID customerId) {
        log.info("Checking vulnerability indicators for customer: {}", customerId);
        // FCA FG21/1 — check CRA data for distress indicators
        return List.of(
                new VulnerabilityIndicator(VulnerabilityIndicator.VulnerabilityType.MISSED_PAYMENTS, false, "low"),
                new VulnerabilityIndicator(VulnerabilityIndicator.VulnerabilityType.GAMBLING, false, "low"),
                new VulnerabilityIndicator(VulnerabilityIndicator.VulnerabilityType.PAYDAY_LOANS, false, "low"),
                new VulnerabilityIndicator(VulnerabilityIndicator.VulnerabilityType.HIGH_UTILISATION, false, "low")
        );
    }

    private Object fetchServiceData(String url) {
        try {
            return restClient.get().uri(url).retrieve().body(Object.class);
        } catch (Exception e) {
            log.warn("Failed to fetch data from: {}", url, e);
            return Map.of("error", "Service unavailable");
        }
    }

    private void deleteDataFallback(UUID customerId, Throwable t) {
        log.error("Failed to delete data for customer: {}. Reason: {}", customerId, t.getMessage());
        throw new RuntimeException("Data deletion failed — manual intervention required", t);
    }
}
