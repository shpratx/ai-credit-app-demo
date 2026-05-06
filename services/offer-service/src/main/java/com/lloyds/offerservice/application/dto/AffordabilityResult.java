package com.lloyds.offerservice.application.dto;

import java.math.BigDecimal;

public record AffordabilityResult(boolean passed, BigDecimal dbrPercent, BigDecimal disposableIncome) {}
