package com.lloyds.creditcoach.alert.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
public class PushNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationClient.class);
    private final RestClient restClient;

    public PushNotificationClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("${app.notification.base-url:http://notification-service:8080}")
                .build();
    }

    @CircuitBreaker(name = "pushNotification", fallbackMethod = "pushFallback")
    @Retry(name = "pushNotification")
    public void sendPush(UUID customerId, String title, String message) {
        log.info("Sending push notification to customer: {}", customerId);
        restClient.post()
                .uri("/api/v1/notifications/push")
                .body(Map.of("customerId", customerId.toString(), "title", title, "body", message))
                .retrieve()
                .toBodilessEntity();
        log.info("Push notification sent to customer: {}", customerId);
    }

    private void pushFallback(UUID customerId, String title, String message, Throwable t) {
        log.warn("Push notification fallback for customer: {}. Reason: {}", customerId, t.getMessage());
        throw new RuntimeException("Push notification delivery failed after retries", t);
    }
}
