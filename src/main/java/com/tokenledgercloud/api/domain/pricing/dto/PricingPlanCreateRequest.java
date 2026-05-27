package com.tokenledgercloud.api.domain.pricing.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PricingPlanCreateRequest(
	@NotBlank String provider,
	@NotBlank String model,
	@NotBlank String currency,

	@NotNull
	@DecimalMin("0.000000")
	BigDecimal promptRate,

	@NotNull
	@DecimalMin("0.000000")
	BigDecimal completionRate,

	@DecimalMin("0.000000")
	BigDecimal reasoningRate,

	@DecimalMin("0.000000")
	BigDecimal cachedPromptRate,

	@NotBlank String version,

	@NotNull OffsetDateTime effectiveFrom,

	OffsetDateTime effectiveTo
) {
}