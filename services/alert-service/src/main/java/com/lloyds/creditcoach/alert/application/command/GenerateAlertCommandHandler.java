package com.lloyds.creditcoach.alert.application.command;

import com.lloyds.creditcoach.alert.domain.model.Alert;
import com.lloyds.creditcoach.alert.domain.port.AlertPreferenceRepository;
import com.lloyds.creditcoach.alert.domain.port.AlertRepository;
import com.lloyds.creditcoach.alert.infrastructure.client.PushNotificationClient;
import com.lloyds.creditcoach.alert.infrastructure.messaging.AlertEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GenerateAlertCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(GenerateAlertCommandHandler.class);
    private final AlertRepository alertRepository;
    private final AlertPreferenceRepository preferenceRepository;
    private final PushNotificationClient pushNotificationClient;
    private final AlertEventPublisher alertEventPublisher;

    public GenerateAlertCommandHandler(AlertRepository alertRepository,
                                       AlertPreferenceRepository preferenceRepository,
                                       PushNotificationClient pushNotificationClient,
                                       AlertEventPublisher alertEventPublisher) {
        this.alertRepository = alertRepository;
        this.preferenceRepository = preferenceRepository;
        this.pushNotificationClient = pushNotificationClient;
        this.alertEventPublisher = alertEventPublisher;
    }

    @Transactional
    public void handle(UUID customerId, Alert.AlertType type, String title, String message, Alert.Severity severity) {
        log.info("Generating alert for customer: {}, type: {}", customerId, type);

        var prefs = preferenceRepository.findByCustomerId(customerId);
        if (prefs.isPresent() && !prefs.get().isAlertTypeEnabled(type)) {
            log.info("Alert type {} disabled for customer: {}", type, customerId);
            return;
        }

        var alert = Alert.create(customerId, type, title, message, severity);
        alertRepository.save(alert);
        alertEventPublisher.publishAlertGenerated(alert);

        try {
            pushNotificationClient.sendPush(customerId, title, message);
            alertEventPublisher.publishAlertDelivered(alert);
        } catch (Exception e) {
            log.warn("Push notification delivery failed for alert: {}", alert.getId(), e);
            alertEventPublisher.publishAlertFailed(alert, e.getMessage());
        }
    }
}
