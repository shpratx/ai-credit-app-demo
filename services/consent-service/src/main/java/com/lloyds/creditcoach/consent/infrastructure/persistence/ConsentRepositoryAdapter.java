package com.lloyds.creditcoach.consent.infrastructure.persistence;

import com.lloyds.creditcoach.consent.domain.model.Consent;
import com.lloyds.creditcoach.consent.domain.port.ConsentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsentRepositoryAdapter implements ConsentRepository {

    private final JpaConsentRepository jpaRepository;

    public ConsentRepositoryAdapter(JpaConsentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Consent save(Consent consent) {
        ConsentEntity entity = toEntity(consent);
        ConsentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Consent> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Consent> findActiveByCustomerAndProvider(UUID customerId, String craProvider) {
        return jpaRepository.findByCustomerIdAndCraProviderAndStatus(customerId, craProvider, "GRANTED")
                .map(this::toDomain);
    }

    @Override
    public List<Consent> findLatestByCustomer(UUID customerId) {
        return jpaRepository.findLatestByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    private ConsentEntity toEntity(Consent consent) {
        var entity = new ConsentEntity();
        entity.setId(consent.getId());
        entity.setCustomerId(consent.getCustomerId());
        entity.setCraProvider(consent.getCraProvider());
        entity.setStatus(consent.getStatus());
        entity.setConsentTextVersion(consent.getConsentTextVersion());
        entity.setConsentTextHash(consent.getConsentTextHash());
        entity.setGrantedAt(consent.getGrantedAt());
        entity.setWithdrawnAt(consent.getWithdrawnAt());
        entity.setChannel(consent.getChannel());
        entity.setIpAddress(consent.getIpAddress());
        entity.setDeviceFingerprint(consent.getDeviceFingerprint());
        entity.setCreatedAt(consent.getCreatedAt());
        return entity;
    }

    private Consent toDomain(ConsentEntity entity) {
        var consent = new Consent();
        consent.setId(entity.getId());
        consent.setCustomerId(entity.getCustomerId());
        consent.setCraProvider(entity.getCraProvider());
        consent.setStatus(entity.getStatus());
        consent.setConsentTextVersion(entity.getConsentTextVersion());
        consent.setConsentTextHash(entity.getConsentTextHash());
        consent.setGrantedAt(entity.getGrantedAt());
        consent.setWithdrawnAt(entity.getWithdrawnAt());
        consent.setChannel(entity.getChannel());
        consent.setIpAddress(entity.getIpAddress());
        consent.setDeviceFingerprint(entity.getDeviceFingerprint());
        consent.setCreatedAt(entity.getCreatedAt());
        return consent;
    }
}
