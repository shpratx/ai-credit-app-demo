package com.lloyds.creditcoach.compliance.application.command;

import com.lloyds.creditcoach.compliance.application.dto.BreathingSpaceResponse;
import com.lloyds.creditcoach.compliance.domain.model.BreathingSpace;
import com.lloyds.creditcoach.compliance.domain.port.BreathingSpaceRepository;
import com.lloyds.creditcoach.compliance.infrastructure.messaging.ComplianceEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ActivateBreathingSpaceCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ActivateBreathingSpaceCommandHandler.class);
    private final BreathingSpaceRepository breathingSpaceRepository;
    private final ComplianceEventPublisher eventPublisher;

    public ActivateBreathingSpaceCommandHandler(BreathingSpaceRepository breathingSpaceRepository,
                                                ComplianceEventPublisher eventPublisher) {
        this.breathingSpaceRepository = breathingSpaceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BreathingSpaceResponse handle(UUID customerId) {
        log.info("Activating breathing space for customer: {}", customerId);

        breathingSpaceRepository.findActiveByCustomerId(customerId).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Breathing space already active");
        });

        var breathingSpace = BreathingSpace.activate(customerId);
        var saved = breathingSpaceRepository.save(breathingSpace);

        eventPublisher.publishBreathingSpaceActivated(customerId, saved.getEndDate());
        log.info("Breathing space activated for customer: {}, expires: {}", customerId, saved.getEndDate());

        return new BreathingSpaceResponse(saved.getId(), saved.getCustomerId(),
                saved.getStartDate(), saved.getEndDate(), saved.getStatus().name());
    }
}
