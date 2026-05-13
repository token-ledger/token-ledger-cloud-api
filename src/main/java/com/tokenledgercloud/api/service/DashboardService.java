package com.tokenledgercloud.api.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.usage.UsageLogRepository;
import com.tokenledgercloud.api.dto.dashboard.DashboardKpiResponse;
import com.tokenledgercloud.api.dto.dashboard.ModelCostSummaryResponse;
import com.tokenledgercloud.api.dto.dashboard.ProjectCostRankingResponse;
import com.tokenledgercloud.api.global.exception.InvalidPeriodException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final UsageLogRepository usageLogRepository;

	@Transactional(readOnly = true)
	public DashboardKpiResponse getKpi(Long projectId, String period) {
		TimeRange range = resolvePeriod(period);
		var row = usageLogRepository.getKpi(projectId, range.from(), range.to());

		return new DashboardKpiResponse(
			row.getTotalCost(),
			row.getTotalTokens(),
			row.getBlockedRequests()
		);
	}

	@Transactional(readOnly = true)
	public List<ModelCostSummaryResponse> getModelCostSummary(Long projectId, String period) {
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

	private record TimeRange(LocalDateTime from, LocalDateTime to) {
	}
}
