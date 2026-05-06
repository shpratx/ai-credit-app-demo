package com.lloyds.creditcoach.creditscore.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DebtOverviewResponse(
        UUID customerId,
        BigDecimal totalDebt,
        BigDecimal totalCreditLimit,
        double utilisationPercent,
        List<DebtAccount> accounts
) {
    public record DebtAccount(
            String accountType,
            String provider,
            BigDecimal balance,
            BigDecimal creditLimit,
            String status
    ) {}
}
