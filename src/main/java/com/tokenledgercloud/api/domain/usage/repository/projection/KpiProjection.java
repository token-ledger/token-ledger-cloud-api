package com.tokenledgercloud.api.domain.usage.repository.projection;

import java.math.BigDecimal;

public interface KpiProjection {

	BigDecimal getTotalCost();

	Long getTotalTokens();

	Long getBlockedRequests();
}
