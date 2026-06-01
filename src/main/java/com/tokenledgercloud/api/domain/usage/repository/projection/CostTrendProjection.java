package com.tokenledgercloud.api.domain.usage.repository.projection;

import java.math.BigDecimal;

public interface CostTrendProjection {
	String getBucket();

	BigDecimal getPromptCostUsd();

	BigDecimal getCompletionCostUsd();

	BigDecimal getReasoningCostUsd();
}