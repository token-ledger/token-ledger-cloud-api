package com.tokenledgercloud.api.domain.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardOverviewResponse(
	Filters filters,
	Kpis kpis,
	List<CostTrend> costTrend,
	List<ProjectRanking> projectRanking,
	List<Event> events
) {
	public record Filters(
		String projectId,
		String environment,
		String period
	) {
	}

	public record Kpis(
		BigDecimal totalCostUsd,
		Long totalTokens,
		BigDecimal budgetUsagePercent,
		BigDecimal budgetRemainingUsd
	) {
	}

	public record CostTrend(
		String bucket,
		BigDecimal promptCostUsd,
		BigDecimal completionCostUsd,
		BigDecimal reasoningCostUsd
	) {
	}

	public record ProjectRanking(
		String projectId,
		String projectName,
		String environment,
		String topModel,
		BigDecimal costUsd,
		BigDecimal budgetUsagePercent
	) {
	}

	public record Event(
		String id,
		LocalDateTime time,
		String projectId,
		String projectName,
		String environment,
		String type,
		String message
	) {
	}
}