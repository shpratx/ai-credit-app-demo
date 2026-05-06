package com.lloyds.creditcoach.alert.infrastructure.persistence;

import com.lloyds.creditcoach.alert.domain.model.Alert;
import com.lloyds.creditcoach.alert.domain.port.AlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AlertRepositoryAdapter implements AlertRepository {

    private final JpaAlertRepository jpaRepository;

    public AlertRepositoryAdapter(JpaAlertRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<Alert> findByCustomerIdOrderByStatusAndCreatedAt(UUID customerId, Pageable pageable) {
        return jpaRepository.findByCustomerIdOrdered(customerId, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Alert> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Alert save(Alert alert) {
        var entity = toEntity(alert);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Alert toDomain(AlertEntity e) {
        var a = new Alert();
        a.setId(e.getId());
        a.setCustomerId(e.getCustomerId());
        a.setType(Alert.AlertType.valueOf(e.getType()));
        a.setTitle(e.getTitle());
        a.setMessage(e.getMessage());
        a.setSeverity(Alert.Severity.valueOf(e.getSeverity()));
        a.setStatus(Alert.AlertStatus.valueOf(e.getStatus()));
        a.setCreatedAt(e.getCreatedAt());
        return a;
    }

    private AlertEntity toEntity(Alert a) {
        var e = new AlertEntity();
        e.setId(a.getId());
        e.setCustomerId(a.getCustomerId());
        e.setType(a.getType().name());
        e.setTitle(a.getTitle());
        e.setMessage(a.getMessage());
        e.setSeverity(a.getSeverity().name());
        e.setStatus(a.getStatus().name());
        e.setCreatedAt(a.getCreatedAt());
        return e;
    }
}
