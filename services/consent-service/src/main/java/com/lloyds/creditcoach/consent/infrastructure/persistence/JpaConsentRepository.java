package com.lloyds.creditcoach.consent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaConsentRepository extends JpaRepository<ConsentEntity, UUID> {

    Optional<ConsentEntity> findByCustomerIdAndCraProviderAndStatus(UUID customerId, String craProvider, String status);

    @Query(value = """
            SELECT DISTINCT ON (cra_provider) * FROM credit_coach_consents
            WHERE customer_id = :customerId
            ORDER BY cra_provider, created_at DESC
            """, nativeQuery = true)
    List<ConsentEntity> findLatestByCustomerId(UUID customerId);
}
