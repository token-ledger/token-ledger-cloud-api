package com.tokenledgercloud.api.domain.usage;

import java.math.BigDecimal;

public interface ProjectCostRankingProjection {

	Long getProjectId();

	BigDecimal getTotalCost();

	Long getTotalTokens();
}