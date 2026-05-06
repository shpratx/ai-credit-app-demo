package com.lloyds.offerservice.domain.port;

import com.lloyds.offerservice.domain.model.PreApprovedOffer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository {
    List<PreApprovedOffer> findAvailableByCustomerId(UUID customerId);
    Optional<PreApprovedOffer> findById(UUID offerId);
    PreApprovedOffer save(PreApprovedOffer offer);
}
