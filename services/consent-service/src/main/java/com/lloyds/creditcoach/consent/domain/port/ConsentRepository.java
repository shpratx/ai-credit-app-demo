package com.lloyds.creditcoach.consent.domain.port;

import com.lloyds.creditcoach.consent.domain.model.Consent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsentRepository {

    Consent save(Consent consent);

    Optional<Consent> findById(UUID id);

    Optional<Consent> findActiveByCustomerAndProvider(UUID customerId, String craProvider);

    List<Consent> findLatestByCustomer(UUID customerId);
}
