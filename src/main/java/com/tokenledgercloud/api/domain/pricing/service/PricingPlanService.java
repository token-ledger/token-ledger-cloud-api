package com.tokenledgercloud.api.domain.pricing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanCreateRequest;
import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanCreateResponse;
import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanItemResponse;
import com.tokenledgercloud.api.domain.pricing.dto.PricingPlanListResponse;
import com.tokenledgercloud.api.domain.pricing.entity.PricingPlan;
import com.tokenledgercloud.api.domain.pricing.repository.PricingPlanRepository;
import com.tokenledgercloud.api.global.exception.InvalidPricingEffectivePeriodException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingPlanService {

	private final PricingPlanRepository pricingPlanRepository;

	@Transactional
	public PricingPlanCreateResponse create(PricingPlanCreateRequest request) {
		validateEffectivePeriod(request);

		PricingPlan plan = PricingPlan.builder()
			.provider(request.provider())
			.model(request.model())
			.currency(request.currency())
			.promptRate(request.promptRate())
			.completionRate(request.completionRate())
			.reasoningRate(safe(request.reasoningRate()))
			.cachedPromptRate(safe(request.cachedPromptRate()))
			.version(request.version())
			.effectiveFrom(toUtcLocalDateTime(request.effectiveFrom()))
			.effectiveTo(toUtcLocalDateTime(request.effectiveTo()))
			.build();

		PricingPlan saved = pricingPlanRepository.save(plan);

		return new PricingPlanCreateResponse(saved.getId());
	}

	@Transactional(readOnly = true)
	public PricingPlanListResponse getPricingPlans(String provider, String model, Boolean activeOnly) {
		List<PricingPlanItemResponse> items =
			pricingPlanRepository.findPricingPlans(
					blankToNull(provider),
					blankToNull(model),
					Boolean.TRUE.equals(activeOnly),
					LocalDateTime.now(ZoneOffset.UTC)
				)
				.stream()
				.map(PricingPlanItemResponse::from)
				.toList();

		return new PricingPlanListResponse(items);
	}

	private void validateEffectivePeriod(PricingPlanCreateRequest request) {
		if (request.effectiveTo() != null && !request.effectiveTo().isAfter(request.effectiveFrom())) {
			throw new InvalidPricingEffectivePeriodException();
		}
	}

	private BigDecimal safe(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private LocalDateTime toUtcLocalDateTime(OffsetDateTime value) {
		return value == null ? null : value.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
	}
}