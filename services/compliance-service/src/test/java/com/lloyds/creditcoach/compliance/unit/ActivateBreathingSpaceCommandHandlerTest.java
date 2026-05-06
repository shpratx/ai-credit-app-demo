package com.lloyds.creditcoach.compliance.unit;

import com.lloyds.creditcoach.compliance.application.command.ActivateBreathingSpaceCommandHandler;
import com.lloyds.creditcoach.compliance.domain.model.BreathingSpace;
import com.lloyds.creditcoach.compliance.domain.port.BreathingSpaceRepository;
import com.lloyds.creditcoach.compliance.infrastructure.messaging.ComplianceEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivateBreathingSpaceCommandHandler")
class ActivateBreathingSpaceCommandHandlerTest {

    @Mock private BreathingSpaceRepository breathingSpaceRepository;
    @Mock private ComplianceEventPublisher eventPublisher;

    private ActivateBreathingSpaceCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ActivateBreathingSpaceCommandHandler(breathingSpaceRepository, eventPublisher);
    }

    @Test
    @DisplayName("should activate 60-day breathing space freeze")
    void should_activate60DayFreeze_when_noActiveBreathingSpace() {
        var customerId = UUID.randomUUID();
        when(breathingSpaceRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.empty());
        when(breathingSpaceRepository.save(any(BreathingSpace.class))).thenAnswer(i -> i.getArgument(0));

        var result = handler.handle(customerId);

        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.endDate()).isEqualTo(LocalDate.now().plusDays(60));
        verify(eventPublisher).publishBreathingSpaceActivated(eq(customerId), any(LocalDate.class));
    }

    @Test
    @DisplayName("should reject when breathing space already active")
    void should_reject_when_breathingSpaceAlreadyActive() {
        var customerId = UUID.randomUUID();
        var existing = BreathingSpace.activate(customerId);
        when(breathingSpaceRepository.findActiveByCustomerId(customerId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> handler.handle(customerId))
                .isInstanceOf(ResponseStatusException.class);
        verify(breathingSpaceRepository, never()).save(any());
    }
}
