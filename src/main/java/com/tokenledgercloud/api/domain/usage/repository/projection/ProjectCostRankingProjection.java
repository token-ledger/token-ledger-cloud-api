package com.tokenledgercloud.api.domain.usage.repository.projection;

import java.math.BigDecimal;

public interface ProjectCostRankingProjection {

	String getProjectId();

	BigDecimal getTotalCost();

	Long getTotalTokens();
}
