package com.tokenledgercloud.api.domain.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.dashboard.dto.DashboardKpiResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.ModelCostSummaryResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.ProjectCostRankingResponse;
import com.tokenledgercloud.api.domain.dashboard.service.DashboardService;
import com.tokenledgercloud.api.global.response.ApiResponse;
import com.tokenledgercloud.api.domain.dashboard.dto.DashboardOverviewResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/kpi")
	public ResponseEntity<ApiResponse<DashboardKpiResponse>> kpi(
		@RequestParam(required = false) String projectId,
		@RequestParam(defaultValue = "today") String period
	) {
		return ResponseEntity.ok(ApiResponse.success(dashboardService.getKpi(projectId, period)));
	}

	@GetMapping("/model-cost-summary")
	public ResponseEntity<ApiResponse<List<ModelCostSummaryResponse>>> modelCostSummary(
		@RequestParam(required = false) String projectId,
		@RequestParam(defaultValue = "week") String period
	) {
		return ResponseEntity.ok(ApiResponse.success(dashboardService.getModelCostSummary(projectId, period)));
	}

	@GetMapping("/project-ranking")
	public ResponseEntity<ApiResponse<List<ProjectCostRankingResponse>>> projectRanking(
		@RequestParam(defaultValue = "month") String period
	) {
		return ResponseEntity.ok(ApiResponse.success(dashboardService.getProjectCostRanking(period)));
	}

	@GetMapping("/overview")
	public ResponseEntity<ApiResponse<DashboardOverviewResponse>> overview(
		@RequestParam(required = false) String projectId,
		@RequestParam(required = false) String environment,
		@RequestParam String period
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"대시보드 개요 조회 성공",
				dashboardService.getOverview(projectId, environment, period)
			)
		);
	}
}
