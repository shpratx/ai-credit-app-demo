package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCreditScoreRepository extends JpaRepository<CreditScoreEntity, UUID> {

    @Query("SELECT s FROM CreditScoreEntity s WHERE s.customerId = :customerId AND s.provider = :provider ORDER BY s.retrievedAt DESC LIMIT 1")
    Optional<CreditScoreEntity> findLatestByCustomerAndProvider(UUID customerId, String provider);

    @Query("SELECT s FROM CreditScoreEntity s WHERE s.customerId = :customerId AND s.provider = :provider AND s.retrievedAt BETWEEN :from AND :to ORDER BY s.retrievedAt ASC")
    List<CreditScoreEntity> findByCustomerAndDateRange(UUID customerId, String provider, Instant from, Instant to);

    @Query("SELECT s FROM CreditScoreEntity s WHERE s.customerId = :customerId AND s.provider = :provider ORDER BY s.retrievedAt DESC LIMIT :limit")
    List<CreditScoreEntity> findTopNByCustomer(UUID customerId, String provider, int limit);

    @Query("SELECT s FROM CreditScoreEntity s WHERE s.customerId = :customerId AND s.retrievedAt = (SELECT MAX(s2.retrievedAt) FROM CreditScoreEntity s2 WHERE s2.customerId = s.customerId AND s2.provider = s.provider)")
    List<CreditScoreEntity> findLatestByCustomerIdGroupedByProvider(UUID customerId);
}
