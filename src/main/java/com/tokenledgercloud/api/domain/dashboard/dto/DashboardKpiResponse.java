package com.tokenledgercloud.api.domain.dashboard.dto;

import java.math.BigDecimal;

public record DashboardKpiResponse(
	BigDecimal totalCost,
	Long totalTokens,
	Long blockedRequests
) {
}
