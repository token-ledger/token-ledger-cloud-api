package com.tokenledgercloud.api.dto.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tokenledgercloud.api.domain.usage.UsageLog;
import com.tokenledgercloud.api.domain.usage.UsageStatus;

public record UsageLogResponse(
	Long id,
	String eventId,
	String idempotencyKey,
	Long projectId,
	Long applicationId,
	Long userId,
	String modelId,
	Long inputTokens,
	Long outputTokens,
	Long totalTokens,
	BigDecimal totalCost,
	String currencyCode,
	UsageStatus status,
	LocalDateTime startedAt,
	LocalDateTime finishedAt,
	Long latencyMs
) {

	public static UsageLogResponse from(UsageLog log) {
		return new UsageLogResponse(
			log.getId(),
			log.getEventId(),
			log.getIdempotencyKey(),
			log.getProjectId(),
			log.getApplicationId(),
			log.getUserId(),
			log.getModelId(),
			log.getInputTokens(),
			log.getOutputTokens(),
			log.getTotalTokens(),
			log.getTotalCost(),
			log.getCurrencyCode(),
			log.getStatus(),
			log.getStartedAt(),
			log.getFinishedAt(),
			log.getLatencyMs()
		);
	}
}