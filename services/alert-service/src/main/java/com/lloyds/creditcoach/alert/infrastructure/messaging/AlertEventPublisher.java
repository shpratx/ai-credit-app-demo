package com.lloyds.creditcoach.alert.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.lloyds.creditcoach.alert.domain.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class AlertEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlertEventPublisher.class);
    private static final String TOPIC = "credit-coach.alert.events";

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    public AlertEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishAlertGenerated(Alert alert) {
        publish("alert.generated", alert, null);
    }

    public void publishAlertDelivered(Alert alert) {
        publish("alert.delivered", alert, null);
    }

    public void publishAlertFailed(Alert alert, String reason) {
        publish("alert.failed", alert, reason);
    }

    private void publish(String eventType, Alert alert, String failureReason) {
        try {
            var payload = objectMapper.writeValueAsString(Map.of(
                    "eventId", UUID.randomUUID().toString(),
                    "eventType", eventType,
                    "timestamp", Instant.now().toString(),
                    "correlationId", MDC.get("correlationId") != null ? MDC.get("correlationId") : "unknown",
                    "source", "alert-service",
                    "data", Map.of(
                            "alertId", alert.getId().toString(),
                            "customerId", alert.getCustomerId().toString(),
                            "type", alert.getType().name(),
                            "severity", alert.getSeverity().name(),
                            "failureReason", failureReason != null ? failureReason : ""
                    )
            ));
            pubSubTemplate.publish(TOPIC, payload, Map.of("eventType", eventType));
            log.info("Published event: {}, alertId: {}", eventType, alert.getId());
        } catch (Exception e) {
            log.error("Failed to publish event: {}", eventType, e);
        }
    }
}
