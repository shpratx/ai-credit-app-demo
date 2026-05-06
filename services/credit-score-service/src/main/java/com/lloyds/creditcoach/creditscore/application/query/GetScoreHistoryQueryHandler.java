package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.DataPointDto;
import com.lloyds.creditcoach.creditscore.application.dto.ScoreHistoryResponse;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class GetScoreHistoryQueryHandler {

    private final CreditScoreRepository scoreRepository;
    private final EncryptionService encryptionService;

    public GetScoreHistoryQueryHandler(CreditScoreRepository scoreRepository, EncryptionService encryptionService) {
        this.scoreRepository = scoreRepository;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public ScoreHistoryResponse handle(UUID customerId, int months) {
        Instant from = Instant.now().minus(months * 30L, ChronoUnit.DAYS);
        var scores = scoreRepository.findByCustomerAndDateRange(customerId, "EXPERIAN", from, Instant.now());

        var dataPoints = scores.stream().map(s -> {
            int decrypted = Integer.parseInt(encryptionService.decrypt(s.getScoreValue()));
            return new DataPointDto(s.getRetrievedAt().atOffset(ZoneOffset.UTC).toLocalDate(), decrypted, s.getBand());
        }).toList();

        return new ScoreHistoryResponse(customerId, "EXPERIAN", dataPoints, months);
    }
}
