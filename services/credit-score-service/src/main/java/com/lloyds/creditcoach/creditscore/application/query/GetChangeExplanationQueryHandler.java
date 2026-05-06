package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.ChangeExplanationResponse;
import com.lloyds.creditcoach.creditscore.application.dto.ContributorDto;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.model.ScoreFactor;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.domain.port.ScoreFactorRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetChangeExplanationQueryHandler {

    private final CreditScoreRepository scoreRepository;
    private final ScoreFactorRepository factorRepository;
    private final EncryptionService encryptionService;

    public GetChangeExplanationQueryHandler(CreditScoreRepository scoreRepository,
                                            ScoreFactorRepository factorRepository,
                                            EncryptionService encryptionService) {
        this.scoreRepository = scoreRepository;
        this.factorRepository = factorRepository;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public Optional<ChangeExplanationResponse> handle(UUID customerId) {
        var scores = scoreRepository.findTopNByCustomer(customerId, "EXPERIAN", 2);
        if (scores.size() < 2) {
            return Optional.empty();
        }

        CreditScore current = scores.get(0);
        CreditScore previous = scores.get(1);

        int currentValue = Integer.parseInt(encryptionService.decrypt(current.getScoreValue()));
        int previousValue = Integer.parseInt(encryptionService.decrypt(previous.getScoreValue()));
        int totalChange = currentValue - previousValue;

        if (totalChange == 0) {
            return Optional.empty();
        }

        var currentFactors = factorRepository.findByScoreId(current.getId());
        var contributors = currentFactors.stream()
                .filter(f -> "negative".equals(f.getDirection()) || "positive".equals(f.getDirection()))
                .map(f -> new ContributorDto(f.getTitle(), estimateImpact(f, totalChange), f.getDescription()))
                .toList();

        return Optional.of(new ChangeExplanationResponse(
                customerId, previousValue, currentValue, totalChange,
                totalChange > 0 ? "up" : "down",
                contributors,
                previous.getRetrievedAt().atOffset(ZoneOffset.UTC).toLocalDate(),
                current.getRetrievedAt().atOffset(ZoneOffset.UTC).toLocalDate()
        ));
    }

    private Integer estimateImpact(ScoreFactor factor, int totalChange) {
        if (factor.getWeightingPercent() == null) return null;
        return (totalChange * factor.getWeightingPercent()) / 100;
    }
}
