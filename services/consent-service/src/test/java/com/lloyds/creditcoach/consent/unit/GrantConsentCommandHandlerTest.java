package com.lloyds.creditcoach.consent.unit;

import com.lloyds.creditcoach.consent.application.command.GrantConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.application.dto.GrantConsentRequest;
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
class GrantConsentCommandHandlerTest {

    @Mock
    private ConsentRepository consentRepository;
    @Mock
    private ConsentEventPublisher eventPublisher;

    private GrantConsentCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GrantConsentCommandHandler(consentRepository, eventPublisher);
    }

    @Test
    void should_grantConsent_when_noActiveConsentExists() {
        UUID customerId = UUID.randomUUID();
        var request = new GrantConsentRequest("EXPERIAN", "1.0",
                "a".repeat(64), "WEB", true);

        when(consentRepository.findActiveByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.empty());
        when(consentRepository.save(any(Consent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ConsentResponse response = handler.handle(request, customerId, "127.0.0.1", "fp-123");

        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.status()).isEqualTo("GRANTED");
        assertThat(response.craProvider()).isEqualTo("EXPERIAN");
        assertThat(response.grantedAt()).isNotNull();
        verify(eventPublisher).publishConsentGranted(any(Consent.class));
    }

    @Test
    void should_throwConflict_when_activeConsentExists() {
        UUID customerId = UUID.randomUUID();
        var request = new GrantConsentRequest("EXPERIAN", "1.0",
                "a".repeat(64), "WEB", true);

        when(consentRepository.findActiveByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.of(new Consent()));

        assertThatThrownBy(() -> handler.handle(request, customerId, "127.0.0.1", "fp-123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Active consent already exists");
    }
}
