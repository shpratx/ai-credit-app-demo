package com.lloyds.offerservice.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * CCA s.55A: Standard European Consumer Credit Information (SECCI).
 * All prescribed fields per Consumer Credit Act 1974 s.55A.
 */
public record SecciDocument(
    UUID offerId,
    // 1. Identity and contact details of the creditor
    String creditorName,
    String creditorAddress,
    String creditorPhone,
    String creditorEmail,
    // 2. Description of the main features of the credit product
    String typeOfCredit,
    BigDecimal totalAmountOfCredit,
    String drawdownConditions,
    int durationMonths,
    String repaymentTerms,
    BigDecimal totalAmountPayable,
    // 3. Costs of the credit
    BigDecimal borrowingRate,
    BigDecimal representativeApr,
    boolean rateFixed,
    BigDecimal totalChargeForCredit,
    // 4. Other important legal aspects
    String rightOfWithdrawal,
    int coolingOffPeriodDays,
    BigDecimal earlyRepaymentFeePercent,
    BigDecimal latePaymentFee,
    // 5. Additional information
    String regulatoryAuthority,
    String complaintsContact
) {}
