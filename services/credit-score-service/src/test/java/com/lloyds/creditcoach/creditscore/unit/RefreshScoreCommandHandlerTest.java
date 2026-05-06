package com.lloyds.creditcoach.creditscore.unit;

import com.lloyds.creditcoach.creditscore.application.command.RefreshScoreCommandHandler;
import com.lloyds.creditcoach.creditscore.application.dto.ScoreRetrievingResponse;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.AuditLogRepository;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.domain.port.ScoreFactorRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.cache.RedisCacheService;
import com.lloyds.creditcoach.creditscore.infrastructure.client.ExperianCraClient;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import com.lloyds.creditcoach.creditscore.infrastructure.messaging.ScoreEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshScoreCommandHandlerTest {

    @Mock private CreditScoreRepository scoreRepository;
    @Mock private ScoreFactorRepository factorRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private ExperianCraClient craClient;
    @Mock private RedisCacheService cacheService;
    @Mock private EncryptionService encryptionService;
    @Mock private ScoreEventPublisher eventPublisher;

    private RefreshScoreCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RefreshScoreCommandHandler(scoreRepository, factorRepository, auditLogRepository,
                craClient, cacheService, encryptionService, eventPublisher);
    }

    @Test
    void should_returnRetrieving_when_noRecentRefresh() {
        UUID customerId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        when(scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.empty());

        ScoreRetrievingResponse response = handler.handleRefresh(customerId, correlationId);

        assertThat(response.status()).isEqualTo("retrieving");
        assertThat(response.estimatedSeconds()).isEqualTo(3);
    }

    @Test
    void should_throwRateLimit_when_refreshedWithin24Hours() {
        UUID customerId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        var recentScore = new CreditScore();
        recentScore.setRetrievedAt(Instant.now().minusSeconds(3600)); // 1 hour ago

        when(scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.of(recentScore));

        assertThatThrownBy(() -> handler.handleRefresh(customerId, correlationId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Max 1 refresh per 24 hours");
    }
}
