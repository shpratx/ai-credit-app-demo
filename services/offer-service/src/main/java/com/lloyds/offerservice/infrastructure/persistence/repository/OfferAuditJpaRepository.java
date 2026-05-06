package com.lloyds.offerservice.infrastructure.persistence.repository;

import com.lloyds.offerservice.infrastructure.persistence.entity.OfferAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferAuditJpaRepository extends JpaRepository<OfferAuditEntity, UUID> {
    List<OfferAuditEntity> findByCustomerIdOrderByTimestampDesc(UUID customerId);
}
