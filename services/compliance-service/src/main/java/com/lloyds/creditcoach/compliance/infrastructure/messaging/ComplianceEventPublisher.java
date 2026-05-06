package com.lloyds.creditcoach.compliance.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Component
public class ComplianceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ComplianceEventPublisher.class);
    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    public ComplianceEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDataDeleted(UUID customerId) {
        publish("credit-coach.compliance.events", "data.deleted", Map.of("customerId", customerId.toString()));
    }

    public void publishBreathingSpaceActivated(UUID customerId, LocalDate endDate) {
        publish("credit-coach.compliance.events", "breathing-space.activated",
                Map.of("customerId", customerId.toString(), "endDate", endDate.toString()));
    }

    private void publish(String topic, String eventType, Map<String, String> data) {
        try {
            var payload = objectMapper.writeValueAsString(Map.of(
                    "eventId", UUID.randomUUID().toString(),
                    "eventType", eventType,
                    "timestamp", Instant.now().toString(),
                    "correlationId", MDC.get("correlationId") != null ? MDC.get("correlationId") : "unknown",
                    "source", "compliance-service",
                    "data", data
            ));
            pubSubTemplate.publish(topic, payload, Map.of("eventType", eventType));
            log.info("Published event: {}", eventType);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", eventType, e);
        }
    }
}
