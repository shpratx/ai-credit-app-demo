package com.lloyds.creditcoach.creditscore.unit;

import com.lloyds.creditcoach.creditscore.application.dto.ScoreResponse;
import com.lloyds.creditcoach.creditscore.application.query.GetScoreQueryHandler;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.cache.RedisCacheService;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetScoreQueryHandlerTest {

    @Mock private CreditScoreRepository scoreRepository;
    @Mock private RedisCacheService cacheService;
    @Mock private EncryptionService encryptionService;

    private GetScoreQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetScoreQueryHandler(scoreRepository, cacheService, encryptionService);
    }

    @Test
    void should_returnCachedScore_when_cacheHit() {
        UUID customerId = UUID.randomUUID();
        var cached = new ScoreResponse(customerId, "EXPERIAN", 742, 999, "good",
                null, null, null, Instant.now(), false, "cache");

        when(cacheService.getScore(customerId)).thenReturn(cached);

        ScoreResponse response = handler.handle(customerId);

        assertThat(response.score()).isEqualTo(742);
        assertThat(response.source()).isEqualTo("cache");
        verify(scoreRepository, never()).findLatestByCustomerAndProvider(any(), any());
    }

    @Test
    void should_queryDatabase_when_cacheMiss() {
        UUID customerId = UUID.randomUUID();
        when(cacheService.getScore(customerId)).thenReturn(null);

        var score = new CreditScore();
        score.setCustomerId(customerId);
        score.setProvider("EXPERIAN");
        score.setScoreValue(Base64.getEncoder().encode("742".getBytes()));
        score.setMaxScore(999);
        score.setBand("good");
        score.setRetrievedAt(Instant.now());
        score.setStale(false);

        when(scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.of(score));
        when(encryptionService.decrypt(any())).thenReturn("742");

        ScoreResponse response = handler.handle(customerId);

        assertThat(response.score()).isEqualTo(742);
        assertThat(response.source()).isEqualTo("database");
        verify(cacheService).putScore(eq(customerId), any());
    }

    @Test
    void should_throwNotFound_when_noScoreExists() {
        UUID customerId = UUID.randomUUID();
        when(cacheService.getScore(customerId)).thenReturn(null);
        when(scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(customerId))
                .isInstanceOf(ResponseStatusException.class);
    }
}
