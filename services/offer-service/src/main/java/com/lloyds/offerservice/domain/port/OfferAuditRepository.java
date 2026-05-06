package com.lloyds.offerservice.domain.port;

import com.lloyds.offerservice.domain.model.OfferAuditEntry;

import java.util.List;
import java.util.UUID;

public interface OfferAuditRepository {
    OfferAuditEntry save(OfferAuditEntry entry);
    List<OfferAuditEntry> findByCustomerId(UUID customerId);
}
