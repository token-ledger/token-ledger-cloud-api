package com.tokenledgercloud.api.domain.budget.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.budget.dto.BudgetSummaryResponse;
import com.tokenledgercloud.api.domain.budget.dto.BudgetUpsertRequest;
import com.tokenledgercloud.api.domain.budget.dto.BudgetUpsertResponse;
import com.tokenledgercloud.api.domain.budget.service.BudgetService;
import com.tokenledgercloud.api.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

	private final BudgetService budgetService;

	@PutMapping
	public ResponseEntity<ApiResponse<BudgetUpsertResponse>> upsert(
		@Valid @RequestBody BudgetUpsertRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"예산 설정 저장 성공",
				budgetService.upsert(request)
			)
		);
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<BudgetSummaryResponse>> summary(
		@RequestParam(required = false) String projectId,
		@RequestParam(required = false) String environment,
		@RequestParam String month
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"예산 요약 조회 성공",
				budgetService.getSummary(projectId, environment, month)
			)
		);
	}
}