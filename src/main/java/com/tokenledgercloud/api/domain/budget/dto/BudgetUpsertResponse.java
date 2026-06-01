package com.tokenledgercloud.api.domain.budget.dto;

import java.math.BigDecimal;
import java.util.List;

public record BudgetUpsertResponse(
	String budgetId,
	String projectId,
	String environment,
	BigDecimal limitUsd,
	List<Integer> thresholds
) {
}