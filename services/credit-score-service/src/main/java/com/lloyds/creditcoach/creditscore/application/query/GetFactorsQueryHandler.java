package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.FactorsResponse;
import com.lloyds.creditcoach.creditscore.application.dto.ScoreFactorDto;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.model.ScoreFactor;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.domain.port.ScoreFactorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.UUID;

@Service
public class GetFactorsQueryHandler {

    private final CreditScoreRepository scoreRepository;
    private final ScoreFactorRepository factorRepository;

    public GetFactorsQueryHandler(CreditScoreRepository scoreRepository, ScoreFactorRepository factorRepository) {
        this.scoreRepository = scoreRepository;
        this.factorRepository = factorRepository;
    }

    @Transactional(readOnly = true)
    public FactorsResponse handle(UUID customerId) {
        CreditScore latest = scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No score found"));

        var factors = factorRepository.findByScoreId(latest.getId());

        var sorted = factors.stream()
                .sorted(Comparator.comparing(ScoreFactor::getDirection).reversed()
                        .thenComparing(f -> impactOrder(f.getImpact())))
                .map(this::toDto)
                .toList();

        long positiveCount = factors.stream().filter(f -> "positive".equals(f.getDirection())).count();
        long negativeCount = factors.stream().filter(f -> "negative".equals(f.getDirection())).count();

        return new FactorsResponse(customerId, sorted, (int) positiveCount, (int) negativeCount, latest.getRetrievedAt());
    }

    private int impactOrder(String impact) {
        return switch (impact) {
            case "high" -> 0;
            case "medium" -> 1;
            case "low" -> 2;
            default -> 3;
        };
    }

    private ScoreFactorDto toDto(ScoreFactor f) {
        return new ScoreFactorDto(f.getId().toString(), f.getCategory(), f.getImpact(),
                f.getDirection(), f.getTitle(), f.getDescription(), f.getWeightingPercent());
    }
}
