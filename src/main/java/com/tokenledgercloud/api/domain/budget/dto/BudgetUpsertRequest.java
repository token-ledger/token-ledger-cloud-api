package com.tokenledgercloud.api.domain.budget.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BudgetUpsertRequest(
	String projectId,
	String environment,

	@NotBlank String periodType,

	@NotNull
	@DecimalMin("0.000001")
	BigDecimal limitUsd,

	List<Integer> thresholds
) {
}