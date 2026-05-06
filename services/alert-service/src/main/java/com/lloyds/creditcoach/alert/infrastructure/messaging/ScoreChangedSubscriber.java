package com.lloyds.creditcoach.alert.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.lloyds.creditcoach.alert.application.command.GenerateAlertCommandHandler;
import com.lloyds.creditcoach.alert.domain.model.Alert;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScoreChangedSubscriber {

    private static final Logger log = LoggerFactory.getLogger(ScoreChangedSubscriber.class);
    private static final int SCORE_DROP_THRESHOLD = 20;

    private final PubSubTemplate pubSubTemplate;
    private final GenerateAlertCommandHandler generateAlertHandler;
    private final ObjectMapper objectMapper;

    public ScoreChangedSubscriber(PubSubTemplate pubSubTemplate,
                                   GenerateAlertCommandHandler generateAlertHandler,
                                   ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.generateAlertHandler = generateAlertHandler;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe() {
        pubSubTemplate.subscribe("credit-coach.score.changed-alert-sub", this::handleMessage);
        log.info("Subscribed to credit-coach.score.changed");
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        try {
            String payload = message.getPubsubMessage().getData().toStringUtf8();
            JsonNode root = objectMapper.readTree(payload);
            UUID customerId = UUID.fromString(root.get("customerId").asText());
            int change = root.get("change").asInt();
            String correlationId = root.has("correlationId") ? root.get("correlationId").asText() : UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);

            log.info("Score changed event: customer={}, change={}", customerId, change);

            if (change <= -SCORE_DROP_THRESHOLD) {
                generateAlertHandler.handle(customerId, Alert.AlertType.SCORE_CHANGE,
                        "Credit Score Dropped",
                        String.format("Your credit score has decreased by %d points", Math.abs(change)),
                        Alert.Severity.HIGH);
            } else if (change < 0) {
                generateAlertHandler.handle(customerId, Alert.AlertType.SCORE_CHANGE,
                        "Credit Score Changed",
                        String.format("Your credit score has decreased by %d points", Math.abs(change)),
                        Alert.Severity.MEDIUM);
            } else if (change > 0) {
                generateAlertHandler.handle(customerId, Alert.AlertType.SCORE_CHANGE,
                        "Credit Score Improved",
                        String.format("Your credit score has increased by %d points", change),
                        Alert.Severity.LOW);
            }

            message.ack();
        } catch (Exception e) {
            log.error("Failed to process score.changed event", e);
            message.nack();
        } finally {
            MDC.remove("correlationId");
        }
    }
}
