package com.tokenledgercloud.api.domain.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProjectUsageRankingProjection {

	Long getProjectId();

	BigDecimal getTotalCost();

	Long getTotalTokens();

	LocalDateTime getLatestUsedAt();
}
