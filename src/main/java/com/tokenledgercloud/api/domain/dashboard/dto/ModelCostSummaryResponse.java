package com.tokenledgercloud.api.domain.dashboard.dto;

import java.math.BigDecimal;

public record ModelCostSummaryResponse(
	String modelId,
	BigDecimal totalCost,
	Long totalTokens
) {
}
