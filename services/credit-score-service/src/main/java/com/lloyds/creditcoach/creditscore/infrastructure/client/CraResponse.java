package com.lloyds.creditcoach.creditscore.infrastructure.client;

import java.util.List;

public record CraResponse(int score, int dataQualityScore, List<CraFactorResponse> factors) {}
