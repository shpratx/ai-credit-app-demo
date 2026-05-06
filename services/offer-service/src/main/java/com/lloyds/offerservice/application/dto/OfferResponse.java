package com.lloyds.offerservice.application.dto;

import com.lloyds.offerservice.domain.model.OfferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OfferResponse(
    UUID id,
    UUID customerId,
    UUID productId,
    BigDecimal amount,
    BigDecimal rate,
    BigDecimal apr,
    int term,
    BigDecimal monthlyPayment,
    BigDecimal totalPayable,
    BigDecimal totalChargeForCredit,
    OfferStatus status,
    Instant validUntil,
    Instant createdAt,
    RepresentativeExample representativeExample
) {
    /** CONC 3.5.5R: representative example with all 6 prescribed values */
    public record RepresentativeExample(
        BigDecimal creditAmount,
        BigDecimal annualRate,
        BigDecimal representativeApr,
        int durationMonths,
        BigDecimal monthlyRepayment,
        BigDecimal totalAmountPayable
    ) {}
}
