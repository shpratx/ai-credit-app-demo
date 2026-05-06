package com.lloyds.creditcoach.plan.application.query;

import com.lloyds.creditcoach.plan.application.dto.SpendingImpactResponse;
import com.lloyds.creditcoach.plan.infrastructure.client.SpendingAgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSpendingImpactQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetSpendingImpactQueryHandler.class);

    private final SpendingAgentClient spendingAgentClient;

    public GetSpendingImpactQueryHandler(SpendingAgentClient spendingAgentClient) {
        this.spendingAgentClient = spendingAgentClient;
    }

    public SpendingImpactResponse handle(UUID customerId) {
        log.info("Getting spending impact for customerId={}", customerId);
        return spendingAgentClient.getSpendingImpact(customerId);
    }
}
