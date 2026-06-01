package com.tokenledgercloud.api.domain.usage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tokenledgercloud.api.domain.usage.entity.UsageLog;

public record UsageEventItemResponse(
	String eventId,
	String projectId,
	String environment,
	String provider,
	String model,
	Long totalTokens,
	BigDecimal totalCostUsd,
	LocalDateTime occurredAt
) {

	public static UsageEventItemResponse from(UsageLog usageLog) {
		return new UsageEventItemResponse(
			usageLog.getId(),
			usageLog.getProjectId(),
			usageLog.getEnvironment(),
			usageLog.getProvider(),
			usageLog.getModel(),
			usageLog.getTotalTokens(),
			usageLog.getTotalCostUsd(),
			usageLog.getOccurredAt()
		);
	}
}