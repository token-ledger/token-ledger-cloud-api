package com.tokenledgercloud.api.dto.project;

import java.math.BigDecimal;
import java.util.List;

import com.tokenledgercloud.api.domain.project.ProjectStatus;

public record ProjectDetailResponse(
	String projectId,
	String name,
	String projectKey,
	ProjectStatus status,
	List<String> environments,
	String defaultModel,
	BigDecimal latestMonthlyCostUsd,
	Long latestMonthlyTokens
) {
}
