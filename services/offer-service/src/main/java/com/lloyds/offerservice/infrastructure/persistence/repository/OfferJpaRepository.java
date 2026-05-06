package com.lloyds.offerservice.infrastructure.persistence.repository;

import com.lloyds.offerservice.infrastructure.persistence.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferJpaRepository extends JpaRepository<OfferEntity, UUID> {
    List<OfferEntity> findByCustomerIdAndStatus(UUID customerId, com.lloyds.offerservice.domain.model.OfferStatus status);
}
