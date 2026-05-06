package com.lloyds.creditcoach.alert.domain.port;

import com.lloyds.creditcoach.alert.domain.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AlertRepository {
    Page<Alert> findByCustomerIdOrderByStatusAndCreatedAt(UUID customerId, Pageable pageable);
    Optional<Alert> findById(UUID id);
    Alert save(Alert alert);
}
