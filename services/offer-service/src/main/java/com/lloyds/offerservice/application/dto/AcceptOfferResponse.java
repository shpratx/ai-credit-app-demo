package com.lloyds.offerservice.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * CCA s.66A: 14-day cooling-off period communicated.
 * CONC 7.7.5R: late fee capped at £12.
 * CCA s.95A: early repayment fee 0% for Lloyds.
 */
public record AcceptOfferResponse(
    UUID offerId,
    String status,
    Instant acceptedAt,
    String coolingOffNotice,
    Instant coolingOffExpiry,
    BigDecimal earlyRepaymentFeePercent,
    BigDecimal latePaymentFeeCap
) {}
