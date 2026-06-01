package com.tokenledgercloud.api.domain.budget.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tokenledgercloud.api.domain.budget.entity.MonthlyBudgetSetting;

@Component
public class BudgetCalculator {

	public BudgetCalculationResult calculate(BigDecimal limitUsd, BigDecimal spentUsd) {
		BigDecimal safeLimitUsd = limitUsd == null ? BigDecimal.ZERO : limitUsd;
		BigDecimal safeSpentUsd = spentUsd == null ? BigDecimal.ZERO : spentUsd;

		BigDecimal remainingUsd = safeLimitUsd.subtract(safeSpentUsd);
		if (remainingUsd.compareTo(BigDecimal.ZERO) < 0) {
			remainingUsd = BigDecimal.ZERO;
		}

		BigDecimal usagePercent = BigDecimal.ZERO;
		if (safeLimitUsd.compareTo(BigDecimal.ZERO) > 0) {
			usagePercent = safeSpentUsd
				.multiply(BigDecimal.valueOf(100))
				.divide(safeLimitUsd, 2, RoundingMode.HALF_UP);
		}

		return new BudgetCalculationResult(
			safeLimitUsd,
			safeSpentUsd,
			remainingUsd,
			usagePercent
		);
	}

	public void applyThresholds(MonthlyBudgetSetting budget, List<Integer> thresholds) {
		List<Integer> safeThresholds = thresholds == null ? List.of(50, 80, 100) : thresholds;

		budget.setThreshold50Enabled(safeThresholds.contains(50));
		budget.setThreshold80Enabled(safeThresholds.contains(80));
		budget.setThreshold100Enabled(safeThresholds.contains(100));
	}

	public List<Integer> toThresholds(MonthlyBudgetSetting budget) {
		List<Integer> thresholds = new ArrayList<>();

		if (Boolean.TRUE.equals(budget.getThreshold50Enabled())) {
			thresholds.add(50);
		}
		if (Boolean.TRUE.equals(budget.getThreshold80Enabled())) {
			thresholds.add(80);
		}
		if (Boolean.TRUE.equals(budget.getThreshold100Enabled())) {
			thresholds.add(100);
		}

		return thresholds;
	}

	public record BudgetCalculationResult(
		BigDecimal limitUsd,
		BigDecimal spentUsd,
		BigDecimal remainingUsd,
		BigDecimal usagePercent
	) {
	}
}