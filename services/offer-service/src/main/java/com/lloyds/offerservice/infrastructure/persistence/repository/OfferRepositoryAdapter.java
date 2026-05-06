package com.lloyds.offerservice.infrastructure.persistence.repository;

import com.lloyds.offerservice.domain.model.OfferStatus;
import com.lloyds.offerservice.domain.model.PreApprovedOffer;
import com.lloyds.offerservice.domain.port.OfferRepository;
import com.lloyds.offerservice.infrastructure.persistence.entity.OfferEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OfferRepositoryAdapter implements OfferRepository {

    private final OfferJpaRepository jpaRepository;

    public OfferRepositoryAdapter(OfferJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<PreApprovedOffer> findAvailableByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdAndStatus(customerId, OfferStatus.AVAILABLE)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<PreApprovedOffer> findById(UUID offerId) {
        return jpaRepository.findById(offerId).map(this::toDomain);
    }

    @Override
    public PreApprovedOffer save(PreApprovedOffer offer) {
        return toDomain(jpaRepository.save(toEntity(offer)));
    }

    private PreApprovedOffer toDomain(OfferEntity e) {
        var o = new PreApprovedOffer();
        o.setId(e.getId());
        o.setCustomerId(e.getCustomerId());
        o.setProductId(e.getProductId());
        o.setAmount(e.getAmount());
        o.setRate(e.getRate());
        o.setApr(e.getApr());
        o.setTerm(e.getTerm());
        o.setMonthlyPayment(e.getMonthlyPayment());
        o.setTotalPayable(e.getTotalPayable());
        o.setTotalChargeForCredit(e.getTotalChargeForCredit());
        o.setStatus(e.getStatus());
        o.setValidUntil(e.getValidUntil());
        o.setCreatedAt(e.getCreatedAt());
        return o;
    }

    private OfferEntity toEntity(PreApprovedOffer o) {
        var e = new OfferEntity();
        e.setId(o.getId());
        e.setCustomerId(o.getCustomerId());
        e.setProductId(o.getProductId());
        e.setAmount(o.getAmount());
        e.setRate(o.getRate());
        e.setApr(o.getApr());
        e.setTerm(o.getTerm());
        e.setMonthlyPayment(o.getMonthlyPayment());
        e.setTotalPayable(o.getTotalPayable());
        e.setTotalChargeForCredit(o.getTotalChargeForCredit());
        e.setStatus(o.getStatus());
        e.setValidUntil(o.getValidUntil());
        e.setCreatedAt(o.getCreatedAt());
        return e;
    }
}
