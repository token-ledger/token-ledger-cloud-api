package com.tokenledgercloud.api.domain.usage.repository.projection;

import java.math.BigDecimal;

public interface ModelCostSummaryProjection {

	String getModelId();

	BigDecimal getTotalCost();

	Long getTotalTokens();
}
