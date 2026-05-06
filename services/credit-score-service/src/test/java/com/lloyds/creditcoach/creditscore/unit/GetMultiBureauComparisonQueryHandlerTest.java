package com.lloyds.creditcoach.creditscore.unit;

import com.lloyds.creditcoach.creditscore.application.query.GetMultiBureauComparisonQueryHandler;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMultiBureauComparisonQueryHandler")
class GetMultiBureauComparisonQueryHandlerTest {

    @Mock private CreditScoreRepository creditScoreRepository;
    @Mock private EncryptionService encryptionService;

    private GetMultiBureauComparisonQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetMultiBureauComparisonQueryHandler(creditScoreRepository, encryptionService);
    }

    @Test
    @DisplayName("should return normalised scores from multiple bureaus")
    void should_returnNormalisedScores_when_multipleBureausAvailable() {
        var customerId = UUID.randomUUID();
        var experianScore = buildScore(customerId, "EXPERIAN", 999);
        var equifaxScore = buildScore(customerId, "EQUIFAX", 1000);

        when(creditScoreRepository.findLatestByCustomerIdGroupedByProvider(customerId))
                .thenReturn(List.of(experianScore, equifaxScore));
        when(encryptionService.decrypt(experianScore.getScoreValue())).thenReturn("750");
        when(encryptionService.decrypt(equifaxScore.getScoreValue())).thenReturn("800");

        var result = handler.handle(customerId);

        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.bureauScores()).hasSize(2);

        var experian = result.bureauScores().stream().filter(b -> b.provider().equals("EXPERIAN")).findFirst().orElseThrow();
        assertThat(experian.score()).isEqualTo(750);
        assertThat(experian.normalisedScore()).isEqualTo(75);

        var equifax = result.bureauScores().stream().filter(b -> b.provider().equals("EQUIFAX")).findFirst().orElseThrow();
        assertThat(equifax.score()).isEqualTo(800);
        assertThat(equifax.normalisedScore()).isEqualTo(80);
    }

    private CreditScore buildScore(UUID customerId, String provider, int maxScore) {
        var cs = new CreditScore();
        cs.setId(UUID.randomUUID());
        cs.setCustomerId(customerId);
        cs.setProvider(provider);
        cs.setScoreValue(provider.getBytes()); // unique per provider for mock matching
        cs.setMaxScore(maxScore);
        cs.setBand("GOOD");
        cs.setRetrievedAt(Instant.now());
        return cs;
    }
}
