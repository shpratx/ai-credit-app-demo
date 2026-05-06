package com.lloyds.creditcoach.alert.unit;

import com.lloyds.creditcoach.alert.application.command.GenerateAlertCommandHandler;
import com.lloyds.creditcoach.alert.domain.model.Alert;
import com.lloyds.creditcoach.alert.domain.model.AlertPreference;
import com.lloyds.creditcoach.alert.domain.port.AlertPreferenceRepository;
import com.lloyds.creditcoach.alert.domain.port.AlertRepository;
import com.lloyds.creditcoach.alert.infrastructure.client.PushNotificationClient;
import com.lloyds.creditcoach.alert.infrastructure.messaging.AlertEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateAlertCommandHandler")
class GenerateAlertCommandHandlerTest {

    @Mock private AlertRepository alertRepository;
    @Mock private AlertPreferenceRepository preferenceRepository;
    @Mock private PushNotificationClient pushNotificationClient;
    @Mock private AlertEventPublisher alertEventPublisher;

    private GenerateAlertCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GenerateAlertCommandHandler(alertRepository, preferenceRepository, pushNotificationClient, alertEventPublisher);
    }

    @Test
    @DisplayName("should generate alert when score drop exceeds threshold")
    void should_generateAlert_when_scoreDropExceedsThreshold() {
        var customerId = UUID.randomUUID();
        when(preferenceRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

        handler.handle(customerId, Alert.AlertType.SCORE_CHANGE, "Score Dropped", "Dropped by 25 points", Alert.Severity.HIGH);

        verify(alertRepository).save(any(Alert.class));
        verify(alertEventPublisher).publishAlertGenerated(any(Alert.class));
        verify(pushNotificationClient).sendPush(eq(customerId), any(), any());
    }

    @Test
    @DisplayName("should not generate alert when preference opt-out is active")
    void should_notGenerateAlert_when_preferenceOptOutActive() {
        var customerId = UUID.randomUUID();
        var pref = new AlertPreference();
        pref.setCustomerId(customerId);
        pref.setScoreChangeEnabled(false);
        when(preferenceRepository.findByCustomerId(customerId)).thenReturn(Optional.of(pref));

        handler.handle(customerId, Alert.AlertType.SCORE_CHANGE, "Score Changed", "Changed", Alert.Severity.MEDIUM);

        verify(alertRepository, never()).save(any());
        verify(pushNotificationClient, never()).sendPush(any(), any(), any());
    }

    @Test
    @DisplayName("should publish alert.failed event when delivery retry exhausted")
    void should_publishAlertFailed_when_deliveryRetryExhausted() {
        var customerId = UUID.randomUUID();
        when(preferenceRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));
        doThrow(new RuntimeException("Push failed")).when(pushNotificationClient).sendPush(any(), any(), any());

        handler.handle(customerId, Alert.AlertType.SCORE_CHANGE, "Score Changed", "Changed", Alert.Severity.LOW);

        verify(alertRepository).save(any(Alert.class));
        verify(alertEventPublisher).publishAlertGenerated(any(Alert.class));
        verify(alertEventPublisher).publishAlertFailed(any(Alert.class), eq("Push failed"));
        verify(alertEventPublisher, never()).publishAlertDelivered(any());
    }
}
