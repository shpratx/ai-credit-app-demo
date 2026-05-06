package com.lloyds.creditcoach.consent.unit;

import com.lloyds.creditcoach.consent.application.command.WithdrawConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.domain.model.Consent;
import com.lloyds.creditcoach.consent.domain.port.ConsentRepository;
import com.lloyds.creditcoach.consent.infrastructure.messaging.ConsentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawConsentCommandHandlerTest {

    @Mock
    private ConsentRepository consentRepository;
    @Mock
    private ConsentEventPublisher eventPublisher;

    private WithdrawConsentCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new WithdrawConsentCommandHandler(consentRepository, eventPublisher);
    }

    @Test
    void should_withdrawConsent_when_consentIsGranted() {
        UUID customerId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        Consent existing = Consent.grant(customerId, "EXPERIAN", "1.0", "a".repeat(64), "WEB", "127.0.0.1", "fp");
        existing.setId(consentId);

        when(consentRepository.findById(consentId)).thenReturn(Optional.of(existing));
        when(consentRepository.save(any(Consent.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsentResponse response = handler.handle(consentId, customerId, "customer_request");

        assertThat(response.status()).isEqualTo("WITHDRAWN");
        assertThat(response.withdrawnAt()).isNotNull();
        verify(eventPublisher).publishConsentRevoked(any(Consent.class));
    }

    @Test
    void should_throwNotFound_when_consentDoesNotExist() {
        UUID consentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(consentRepository.findById(consentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(consentId, customerId, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consent not found");
    }

    @Test
    void should_throwConflict_when_alreadyWithdrawn() {
        UUID customerId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        Consent existing = Consent.grant(customerId, "EXPERIAN", "1.0", "a".repeat(64), "WEB", "127.0.0.1", "fp");
        existing.setId(consentId);
        Consent withdrawn = existing.withdraw();
        withdrawn.setId(consentId);

        when(consentRepository.findById(consentId)).thenReturn(Optional.of(withdrawn));

        assertThatThrownBy(() -> handler.handle(consentId, customerId, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already withdrawn");
    }
}
