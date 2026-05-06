package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreditScoreRepositoryAdapter implements CreditScoreRepository {

    private final JpaCreditScoreRepository jpaRepository;

    public CreditScoreRepositoryAdapter(JpaCreditScoreRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CreditScore save(CreditScore score) {
        var entity = toEntity(score);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CreditScore> findLatestByCustomerAndProvider(UUID customerId, String provider) {
        return jpaRepository.findLatestByCustomerAndProvider(customerId, provider).map(this::toDomain);
    }

    @Override
    public List<CreditScore> findByCustomerAndDateRange(UUID customerId, String provider, Instant from, Instant to) {
        return jpaRepository.findByCustomerAndDateRange(customerId, provider, from, to).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<CreditScore> findTopNByCustomer(UUID customerId, String provider, int limit) {
        return jpaRepository.findTopNByCustomer(customerId, provider, limit).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<CreditScore> findLatestByCustomerIdGroupedByProvider(UUID customerId) {
        return jpaRepository.findLatestByCustomerIdGroupedByProvider(customerId).stream()
                .map(this::toDomain).toList();
    }

    private CreditScoreEntity toEntity(CreditScore s) {
        var e = new CreditScoreEntity();
        e.setId(s.getId());
        e.setCustomerId(s.getCustomerId());
        e.setProvider(s.getProvider());
        e.setScoreValue(s.getScoreValue());
        e.setMaxScore(s.getMaxScore());
        e.setBand(s.getBand());
        e.setPreviousScore(s.getPreviousScore());
        e.setChange(s.getChange());
        e.setChangeDirection(s.getChangeDirection());
        e.setRetrievedAt(s.getRetrievedAt());
        e.setStale(s.isStale());
        e.setDataQualityScore(s.getDataQualityScore());
        e.setCreatedAt(s.getCreatedAt());
        return e;
    }

    private CreditScore toDomain(CreditScoreEntity e) {
        var s = new CreditScore();
        s.setId(e.getId());
        s.setCustomerId(e.getCustomerId());
        s.setProvider(e.getProvider());
        s.setScoreValue(e.getScoreValue());
        s.setMaxScore(e.getMaxScore());
        s.setBand(e.getBand());
        s.setPreviousScore(e.getPreviousScore());
        s.setChange(e.getChange());
        s.setChangeDirection(e.getChangeDirection());
        s.setRetrievedAt(e.getRetrievedAt());
        s.setStale(e.isStale());
        s.setDataQualityScore(e.getDataQualityScore());
        s.setCreatedAt(e.getCreatedAt());
        return s;
    }
}
