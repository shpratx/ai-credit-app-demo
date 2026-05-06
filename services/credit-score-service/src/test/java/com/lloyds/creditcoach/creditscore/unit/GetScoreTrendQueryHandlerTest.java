package com.lloyds.creditcoach.creditscore.unit;

import com.lloyds.creditcoach.creditscore.application.query.GetScoreTrendQueryHandler;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetScoreTrendQueryHandler")
class GetScoreTrendQueryHandlerTest {

    @Mock private CreditScoreRepository scoreRepository;
    @Mock private EncryptionService encryptionService;

    private GetScoreTrendQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetScoreTrendQueryHandler(scoreRepository, encryptionService);
    }

    @Test
    @DisplayName("should return trend with statistics when scores exist")
    void should_returnTrend_when_scoresExist() {
        var customerId = UUID.randomUUID();
        var score1 = buildScore(customerId, "742", Instant.now().minus(30, ChronoUnit.DAYS));
        var score2 = buildScore(customerId, "750", Instant.now());

        when(scoreRepository.findByCustomerAndDateRange(eq(customerId), eq("EXPERIAN"), any(), any()))
                .thenReturn(List.of(score2, score1));
        when(encryptionService.decrypt(score1.getScoreValue())).thenReturn("742");
        when(encryptionService.decrypt(score2.getScoreValue())).thenReturn("750");

        var result = handler.handle(customerId, 12);

        assertThat(result.currentScore()).isEqualTo(750);
        assertThat(result.lowestScore()).isEqualTo(742);
        assertThat(result.highestScore()).isEqualTo(750);
        assertThat(result.overallTrend()).isEqualTo("improving");
        assertThat(result.dataPoints()).hasSize(2);
    }

    @Test
    @DisplayName("should return insufficient_data when no scores")
    void should_returnInsufficientData_when_noScores() {
        var customerId = UUID.randomUUID();
        when(scoreRepository.findByCustomerAndDateRange(eq(customerId), eq("EXPERIAN"), any(), any()))
                .thenReturn(List.of());

        var result = handler.handle(customerId, 12);

        assertThat(result.overallTrend()).isEqualTo("insufficient_data");
        assertThat(result.dataPoints()).isEmpty();
    }

    @Test
    @DisplayName("should detect declining trend")
    void should_detectDecliningTrend_when_scoreDecreases() {
        var customerId = UUID.randomUUID();
        var score1 = buildScore(customerId, "780", Instant.now().minus(60, ChronoUnit.DAYS));
        var score2 = buildScore(customerId, "720", Instant.now());

        when(scoreRepository.findByCustomerAndDateRange(eq(customerId), eq("EXPERIAN"), any(), any()))
                .thenReturn(List.of(score2, score1));
        when(encryptionService.decrypt(score1.getScoreValue())).thenReturn("780");
        when(encryptionService.decrypt(score2.getScoreValue())).thenReturn("720");

        var result = handler.handle(customerId, 12);

        assertThat(result.overallTrend()).isEqualTo("declining");
    }

    private CreditScore buildScore(UUID customerId, String scoreValue, Instant retrievedAt) {
        var score = new CreditScore();
        score.setCustomerId(customerId);
        score.setProvider("EXPERIAN");
        score.setScoreValue(scoreValue.getBytes());
        score.setMaxScore(999);
        score.setBand(CreditScore.classifyBand(Integer.parseInt(scoreValue)));
        score.setRetrievedAt(retrievedAt);
        return score;
    }
}
