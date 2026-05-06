package com.lloyds.creditcoach.consent.application.dto;

import jakarta.validation.constraints.Pattern;

public record WithdrawConsentRequest(
        @Pattern(regexp = "customer_request|data_concern|no_longer_needed|other") String reason
) {}
