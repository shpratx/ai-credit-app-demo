package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.MultiBureauResponse;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class GetMultiBureauComparisonQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetMultiBureauComparisonQueryHandler.class);
    private final CreditScoreRepository creditScoreRepository;
    private final EncryptionService encryptionService;

    public GetMultiBureauComparisonQueryHandler(CreditScoreRepository creditScoreRepository,
                                               EncryptionService encryptionService) {
        this.creditScoreRepository = creditScoreRepository;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public MultiBureauResponse handle(UUID customerId) {
        log.info("Fetching multi-bureau comparison for customer: {}", customerId);

        List<CreditScore> scores = creditScoreRepository.findLatestByCustomerIdGroupedByProvider(customerId);

        List<MultiBureauResponse.BureauScore> bureauScores = scores.stream()
                .map(score -> {
                    int decryptedScore = Integer.parseInt(encryptionService.decrypt(score.getScoreValue()));
                    return new MultiBureauResponse.BureauScore(
                            score.getProvider(),
                            decryptedScore,
                            score.getMaxScore(),
                            score.getBand(),
                            normaliseScore(decryptedScore, score.getMaxScore()),
                            score.getRetrievedAt());
                })
                .toList();

        return new MultiBureauResponse(customerId, bureauScores, Instant.now());
    }

    private int normaliseScore(int score, int maxScore) {
        return Math.round((float) score / maxScore * 100);
    }
}
