package com.lloyds.creditcoach.alert.application.query;

import com.lloyds.creditcoach.alert.application.dto.AlertResponse;
import com.lloyds.creditcoach.alert.domain.model.Alert;
import com.lloyds.creditcoach.alert.domain.port.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetAlertsQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetAlertsQueryHandler.class);
    private final AlertRepository alertRepository;

    public GetAlertsQueryHandler(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public Page<AlertResponse> handle(UUID customerId, int page, int size) {
        log.info("Fetching alerts for customer: {}", customerId);
        return alertRepository.findByCustomerIdOrderByStatusAndCreatedAt(customerId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    private AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(), alert.getCustomerId(),
                alert.getType().name(), alert.getTitle(), alert.getMessage(),
                alert.getSeverity().name(), alert.getStatus().name(), alert.getCreatedAt());
    }
}
