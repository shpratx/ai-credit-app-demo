package com.lloyds.creditcoach.alert.application.command;

import com.lloyds.creditcoach.alert.domain.port.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class DismissAlertCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DismissAlertCommandHandler.class);
    private final AlertRepository alertRepository;

    public DismissAlertCommandHandler(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void handle(UUID customerId, UUID alertId) {
        log.info("Dismissing alert: {} for customer: {}", alertId, customerId);
        var alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        if (!alert.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Alert does not belong to customer");
        }
        alert.dismiss();
        alertRepository.save(alert);
        log.info("Alert dismissed: {}", alertId);
    }
}
