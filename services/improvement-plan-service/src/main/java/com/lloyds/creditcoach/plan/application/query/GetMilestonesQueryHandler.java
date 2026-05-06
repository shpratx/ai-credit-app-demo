package com.lloyds.creditcoach.plan.application.query;

import com.lloyds.creditcoach.plan.application.dto.MilestoneResponse;
import com.lloyds.creditcoach.plan.domain.port.MilestoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetMilestonesQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetMilestonesQueryHandler.class);

    private final MilestoneRepository milestoneRepository;

    public GetMilestonesQueryHandler(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> handle(UUID customerId) {
        log.info("Getting milestones for customerId={}", customerId);

        return milestoneRepository.findByCustomerId(customerId).stream()
                .map(m -> new MilestoneResponse(
                        m.getId(),
                        m.getType().name().toLowerCase(),
                        m.getTitle(),
                        m.getDescription(),
                        m.getAchievedAt(),
                        m.getScoreAtAchievement(),
                        m.getTargetScore()
                )).toList();
    }
}
