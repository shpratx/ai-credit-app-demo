package com.lloyds.creditcoach.creditscore.application.query;

import com.lloyds.creditcoach.creditscore.application.dto.DebtOverviewResponse;
import com.lloyds.creditcoach.creditscore.infrastructure.client.ExperianCraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class GetDebtOverviewQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetDebtOverviewQueryHandler.class);

    private final ExperianCraClient craClient;

    public GetDebtOverviewQueryHandler(ExperianCraClient craClient) {
        this.craClient = craClient;
    }

    @Transactional(readOnly = true)
    public DebtOverviewResponse handle(UUID customerId) {
        log.info("Getting debt overview for customerId={}", customerId);

        // In production: call CRA for debt data and cache in debt_accounts table
        // Placeholder with representative structure
        var accounts = List.of(
                new DebtOverviewResponse.DebtAccount(
                        "credit_card", "Lloyds", BigDecimal.valueOf(2500),
                        BigDecimal.valueOf(5000), "active"),
                new DebtOverviewResponse.DebtAccount(
                        "personal_loan", "Halifax", BigDecimal.valueOf(8000),
                        BigDecimal.valueOf(10000), "active")
        );

        BigDecimal totalDebt = accounts.stream()
                .map(DebtOverviewResponse.DebtAccount::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLimit = accounts.stream()
                .map(DebtOverviewResponse.DebtAccount::creditLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double utilisation = totalLimit.compareTo(BigDecimal.ZERO) > 0
                ? totalDebt.divide(totalLimit, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0.0;

        return new DebtOverviewResponse(customerId, totalDebt, totalLimit, utilisation, accounts);
    }
}
