package com.tokenledgercloud.api.dto.project;

import java.math.BigDecimal;

public record ProjectRankingItemResponse(
	String projectId,
	String projectName,
	String environment,
	String topModel,
	BigDecimal costUsd,
	int budgetUsagePercent,
	int rank
) {
}
