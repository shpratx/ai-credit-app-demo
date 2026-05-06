package com.lloyds.offerservice.infrastructure.persistence.repository;

import com.lloyds.offerservice.domain.model.OfferAuditEntry;
import com.lloyds.offerservice.domain.port.OfferAuditRepository;
import com.lloyds.offerservice.infrastructure.persistence.entity.OfferAuditEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OfferAuditRepositoryAdapter implements OfferAuditRepository {

    private final OfferAuditJpaRepository jpaRepository;

    public OfferAuditRepositoryAdapter(OfferAuditJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OfferAuditEntry save(OfferAuditEntry entry) {
        var entity = new OfferAuditEntity();
        entity.setId(entry.getId());
        entity.setOfferId(entry.getOfferId());
        entity.setCustomerId(entry.getCustomerId());
        entity.setAction(entry.getAction());
        entity.setTimestamp(entry.getTimestamp());
        entity.setReason(entry.getReason());
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<OfferAuditEntry> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByTimestampDesc(customerId)
                .stream().map(this::toDomain).toList();
    }

    private OfferAuditEntry toDomain(OfferAuditEntity e) {
        var entry = new OfferAuditEntry();
        entry.setId(e.getId());
        entry.setOfferId(e.getOfferId());
        entry.setCustomerId(e.getCustomerId());
        entry.setAction(e.getAction());
        entry.setTimestamp(e.getTimestamp());
        entry.setReason(e.getReason());
        return entry;
    }
}
