package com.lloyds.offerservice.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class OfferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OfferEventPublisher.class);
    private static final String TOPIC = "credit-coach.offer.events";

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    public OfferEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishOfferPresented(UUID offerId, UUID customerId) {
        publish("offer.presented", offerId, customerId);
    }

    public void publishOfferAccepted(UUID offerId, UUID customerId) {
        publish("offer.accepted", offerId, customerId);
    }

    public void publishOfferSuppressed(UUID offerId, UUID customerId, String reason) {
        publish("offer.suppressed", offerId, customerId);
    }

    private void publish(String eventType, UUID offerId, UUID customerId) {
        try {
            var event = Map.of(
                    "eventId", UUID.randomUUID().toString(),
                    "eventType", eventType,
                    "timestamp", Instant.now().toString(),
                    "correlationId", MDC.get("correlationId") != null ? MDC.get("correlationId") : "",
                    "offerId", offerId.toString(),
                    "customerId", customerId.toString());
            String payload = objectMapper.writeValueAsString(event);
            pubSubTemplate.publish(TOPIC, payload, Map.of("eventType", eventType));
            log.info("Published event: type={}, offerId={}", eventType, offerId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize offer event: {}", eventType, e);
        }
    }
}
