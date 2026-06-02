package com.tokenledgercloud.api.domain.pricing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanCreateRequest;
import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanCreateResponse;
import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanListResponse;
import com.tokenledgercloud.api.domain.pricing.service.PricingPlanService;
import com.tokenledgercloud.api.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pricing-plans")
public class PricingPlanController {

	private final PricingPlanService pricingPlanService;

	@GetMapping
	public ResponseEntity<ApiResponse<PricingPlanListResponse>> getPricingPlans(
		@RequestParam(required = false) String provider,
		@RequestParam(required = false) String model,
		@RequestParam(required = false, defaultValue = "false") Boolean activeOnly
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"가격표 목록 조회 성공",
				pricingPlanService.getPricingPlans(provider, model, activeOnly)
			)
		);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PricingPlanCreateResponse>> create(
		@Valid @RequestBody PricingPlanCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(
				"가격표 등록 성공",
				pricingPlanService.create(request)
			));
	}
}