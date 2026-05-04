package com.tokenledgercloud.api.dto.dashboard;

import java.math.BigDecimal;

public record ModelCostSummaryResponse(
	String modelId,
	BigDecimal totalCost,
	Long totalTokens
) {
}