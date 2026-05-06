package com.lloyds.creditcoach.creditscore.domain.port;

import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditScoreRepository {

    CreditScore save(CreditScore score);

    Optional<CreditScore> findLatestByCustomerAndProvider(UUID customerId, String provider);

    List<CreditScore> findByCustomerAndDateRange(UUID customerId, String provider, Instant from, Instant to);

    List<CreditScore> findTopNByCustomer(UUID customerId, String provider, int limit);

    List<CreditScore> findLatestByCustomerIdGroupedByProvider(UUID customerId);
}
