package com.tokenledgercloud.api.domain.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.budget.service.BudgetService;
import com.tokenledgercloud.api.domain.dashboard.dto.DashboardKpiResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.DashboardOverviewResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.ModelCostSummaryResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.ProjectCostRankingResponse;
import com.tokenledgercloud.api.domain.event.repository.OperationalEventRepository;
import com.tokenledgercloud.api.domain.usage.repository.UsageLogRepository;
import com.tokenledgercloud.api.global.exception.ApiException;
import com.tokenledgercloud.api.global.exception.ErrorCode;
import com.tokenledgercloud.api.global.exception.InvalidPeriodException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final UsageLogRepository usageLogRepository;
	private final OperationalEventRepository operationalEventRepository;
	private final BudgetService budgetService;

	@Transactional(readOnly = true)
	public DashboardKpiResponse getKpi(String projectId, String period) {
		TimeRange range = resolvePeriod(period);
		var row = usageLogRepository.getKpi(projectId, range.from(), range.to());

		return new DashboardKpiResponse(
			row.getTotalCost(),
			row.getTotalTokens(),
			row.getBlockedRequests()
		);
	}

	@Transactional(readOnly = true)
	public List<ModelCostSummaryResponse> getModelCostSummary(String projectId, String period) {
		TimeRange range = resolvePeriod(period);

		return usageLogRepository.findModelCostSummary(projectId, range.from(), range.to())
			.stream()
			.map(row -> new ModelCostSummaryResponse(
				row.getModelId(),
				row.getTotalCost(),
				row.getTotalTokens()
			))
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectCostRankingResponse> getProjectCostRanking(String period) {
		TimeRange range = resolvePeriod(period);

		return usageLogRepository.findProjectCostRanking(range.from(), range.to())
			.stream()
			.map(row -> new ProjectCostRankingResponse(
				row.getProjectId(),
				row.getTotalCost(),
				row.getTotalTokens()
			))
			.toList();
	}

	@Transactional(readOnly = true)
	public DashboardOverviewResponse getOverview(String projectId, String environment, String period) {
		TimeRange range = resolvePeriod(period);

		var kpi = usageLogRepository.getKpi(projectId, range.from(), range.to());

		var costTrend = usageLogRepository.findCostTrend(projectId, environment, range.from(), range.to())
			.stream()
			.map(row -> new DashboardOverviewResponse.CostTrend(
				row.getBucket(),
				row.getPromptCostUsd(),
				row.getCompletionCostUsd(),
				row.getReasoningCostUsd()
			))
			.toList();

		var topModelMap = usageLogRepository.findTopModelsByProject(environment, range.from(), range.to())
			.stream()
			.collect(Collectors.toMap(
				row -> row.getProjectId(),
				row -> row.getTopModel()
			));

		var ranking = usageLogRepository.findProjectCostRanking(range.from(), range.to())
			.stream()
			.map(row -> new DashboardOverviewResponse.ProjectRanking(
				row.getProjectId(),
				row.getProjectId(),
				environment,
				topModelMap.get(row.getProjectId()),
				row.getTotalCost(),
				BigDecimal.ZERO
			))
			.toList();

		var events = operationalEventRepository.findEvents(
				blankToNull(projectId),
				blankToNull(environment),
				null,
				null,
				PageRequest.of(0, 20)
			)
			.stream()
			.map(event -> new DashboardOverviewResponse.Event(
				event.getId(),
				event.getOccurredAt(),
				event.getProjectId(),
				event.getProjectId(),
				event.getEnvironment(),
				event.getEventType(),
				event.getMessage()
			))
			.toList();

		BudgetKpi budgetKpi = resolveBudgetKpi(projectId, environment);

		return new DashboardOverviewResponse(
			new DashboardOverviewResponse.Filters(projectId, environment, period),
			new DashboardOverviewResponse.Kpis(
				kpi.getTotalCost(),
				kpi.getTotalTokens(),
				budgetKpi.budgetUsagePercent(),
				budgetKpi.budgetRemainingUsd()
			),
			costTrend,
			ranking,
			events
		);
	}

	private BudgetKpi resolveBudgetKpi(String projectId, String environment) {
		String currentMonth = LocalDate.now().toString().substring(0, 7);

		try {
			var budgetSummary = budgetService.getSummary(projectId, environment, currentMonth);

			return new BudgetKpi(
				budgetSummary.usagePercent(),
				budgetSummary.remainingUsd()
			);
		} catch (ApiException exception) {
			if (exception.getErrorCode() == ErrorCode.BUDGET_NOT_FOUND) {
				return new BudgetKpi(BigDecimal.ZERO, BigDecimal.ZERO);
			}

			throw exception;
		}
	}

	private TimeRange resolvePeriod(String period) {
		LocalDate today = LocalDate.now();

		return switch (period == null ? "today" : period) {
			case "week" -> new TimeRange(
				today.minusDays(6).atStartOfDay(),
				today.plusDays(1).atStartOfDay()
			);
			case "month" -> new TimeRange(
				today.withDayOfMonth(1).atStartOfDay(),
				today.plusDays(1).atStartOfDay()
			);
			case "today" -> new TimeRange(
				today.atStartOfDay(),
				today.plusDays(1).atStartOfDay()
			);
			default -> throw new InvalidPeriodException();
		};
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private record TimeRange(LocalDateTime from, LocalDateTime to) {
	}

	private record BudgetKpi(
		BigDecimal budgetUsagePercent,
		BigDecimal budgetRemainingUsd
	) {
	}
}