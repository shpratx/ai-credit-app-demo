package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.ScoreTrendResponse;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GetScoreTrendQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetScoreTrendQueryHandler.class);

    private final CreditScoreRepository scoreRepository;
    private final EncryptionService encryptionService;

    public GetScoreTrendQueryHandler(CreditScoreRepository scoreRepository,
                                     EncryptionService encryptionService) {
        this.scoreRepository = scoreRepository;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public ScoreTrendResponse handle(UUID customerId, int months) {
        log.info("Getting score trend for customerId={}, months={}", customerId, months);

        Instant from = Instant.now().minus(months * 30L, ChronoUnit.DAYS);
        List<CreditScore> scores = scoreRepository.findByCustomerAndDateRange(
                customerId, "EXPERIAN", from, Instant.now());

        if (scores.isEmpty()) {
            return new ScoreTrendResponse(customerId, "EXPERIAN", months,
                    null, null, null, null, "insufficient_data", List.of(), List.of());
        }

        List<ScoreTrendResponse.TrendDataPoint> dataPoints = new ArrayList<>();
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        long sum = 0;

        for (CreditScore score : scores) {
            int decrypted = Integer.parseInt(encryptionService.decrypt(score.getScoreValue()));
            lowest = Math.min(lowest, decrypted);
            highest = Math.max(highest, decrypted);
            sum += decrypted;
            dataPoints.add(new ScoreTrendResponse.TrendDataPoint(
                    LocalDate.ofInstant(score.getRetrievedAt(), ZoneOffset.UTC),
                    decrypted,
                    score.getBand()
            ));
        }

        int average = (int) (sum / scores.size());
        int firstScore = Integer.parseInt(encryptionService.decrypt(scores.getLast().getScoreValue()));
        int lastScore = Integer.parseInt(encryptionService.decrypt(scores.getFirst().getScoreValue()));
        String trend = lastScore > firstScore ? "improving"
                : lastScore < firstScore ? "declining" : "stable";

        // Generate annotations for significant changes
        List<ScoreTrendResponse.TrendAnnotation> annotations = new ArrayList<>();
        if (highest - lowest > 50) {
            annotations.add(new ScoreTrendResponse.TrendAnnotation(
                    dataPoints.getFirst().date(), "Significant score movement detected", "info"));
        }

        return new ScoreTrendResponse(customerId, "EXPERIAN", months,
                lastScore, lowest, highest, average, trend, dataPoints, annotations);
    }
}
