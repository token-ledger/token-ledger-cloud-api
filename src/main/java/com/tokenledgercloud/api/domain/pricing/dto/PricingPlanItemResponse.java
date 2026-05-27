package com.tokenledgercloud.api.domain.pricing.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.tokenledgercloud.api.domain.pricing.entity.PricingPlan;

public record PricingPlanItemResponse(
	String pricingPlanId,
	String provider,
	String model,
	String currency,
	BigDecimal promptRate,
	BigDecimal completionRate,
	BigDecimal reasoningRate,
	String version,
	OffsetDateTime effectiveFrom,
	OffsetDateTime effectiveTo
) {
	public static PricingPlanItemResponse from(PricingPlan plan) {
		return new PricingPlanItemResponse(
			plan.getId(),
			plan.getProvider(),
			plan.getModel(),
			plan.getCurrency(),
			plan.getPromptRate(),
			plan.getCompletionRate(),
			plan.getReasoningRate(),
			plan.getVersion(),
			toUtcOffsetDateTime(plan.getEffectiveFrom()),
			toUtcOffsetDateTime(plan.getEffectiveTo())
		);
	}

	private static OffsetDateTime toUtcOffsetDateTime(java.time.LocalDateTime value) {
		return value == null ? null : value.atOffset(ZoneOffset.UTC);
	}
}