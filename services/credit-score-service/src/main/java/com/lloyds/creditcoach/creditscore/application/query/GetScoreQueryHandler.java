package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.ScoreResponse;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.cache.RedisCacheService;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class GetScoreQueryHandler {

    private final CreditScoreRepository scoreRepository;
    private final RedisCacheService cacheService;
    private final EncryptionService encryptionService;

    public GetScoreQueryHandler(CreditScoreRepository scoreRepository,
                                RedisCacheService cacheService,
                                EncryptionService encryptionService) {
        this.scoreRepository = scoreRepository;
        this.cacheService = cacheService;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public ScoreResponse handle(UUID customerId) {
        // Cache-first
        ScoreResponse cached = cacheService.getScore(customerId);
        if (cached != null) {
            return cached;
        }

        // DB fallback
        CreditScore latest = scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No score found"));

        int decryptedScore = Integer.parseInt(encryptionService.decrypt(latest.getScoreValue()));
        Integer previousDecrypted = latest.getPreviousScore() != null
                ? Integer.parseInt(encryptionService.decrypt(latest.getPreviousScore()))
                : null;

        var response = new ScoreResponse(customerId, "EXPERIAN", decryptedScore, latest.getMaxScore(),
                latest.getBand(), previousDecrypted, latest.getChange(), latest.getChangeDirection(),
                latest.getRetrievedAt(), latest.isStale(), "database");

        // Warm cache
        cacheService.putScore(customerId, response);
        return response;
    }
}
