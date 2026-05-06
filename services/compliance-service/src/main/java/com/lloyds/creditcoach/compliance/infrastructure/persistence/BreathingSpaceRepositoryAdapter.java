package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import com.lloyds.creditcoach.compliance.domain.model.BreathingSpace;
import com.lloyds.creditcoach.compliance.domain.port.BreathingSpaceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BreathingSpaceRepositoryAdapter implements BreathingSpaceRepository {

    private final JpaBreathingSpaceRepository jpaRepository;

    public BreathingSpaceRepositoryAdapter(JpaBreathingSpaceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BreathingSpace save(BreathingSpace bs) {
        var entity = toEntity(bs);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<BreathingSpace> findActiveByCustomerId(UUID customerId) {
        return jpaRepository.findActiveByCustomerId(customerId).map(this::toDomain);
    }

    private BreathingSpace toDomain(BreathingSpaceEntity e) {
        var bs = new BreathingSpace();
        bs.setId(e.getId());
        bs.setCustomerId(e.getCustomerId());
        bs.setStartDate(e.getStartDate());
        bs.setEndDate(e.getEndDate());
        bs.setStatus(BreathingSpace.BreathingSpaceStatus.valueOf(e.getStatus()));
        bs.setNotifiedAt(e.getNotifiedAt());
        return bs;
    }

    private BreathingSpaceEntity toEntity(BreathingSpace bs) {
        var e = new BreathingSpaceEntity();
        e.setId(bs.getId());
        e.setCustomerId(bs.getCustomerId());
        e.setStartDate(bs.getStartDate());
        e.setEndDate(bs.getEndDate());
        e.setStatus(bs.getStatus().name());
        e.setNotifiedAt(bs.getNotifiedAt());
        return e;
    }
}
