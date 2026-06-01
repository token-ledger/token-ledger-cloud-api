package com.tokenledgercloud.api.domain.usage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tokenledgercloud.api.domain.usage.entity.UsageLog;

public record UsageLogResponse(
	String id,
	String organizationId,
	String projectId,
	String apiKeyId,
	String environment,
	String requestId,
	String provider,
	String model,
	Long promptTokens,
	Long completionTokens,
	Long reasoningTokens,
	Long cachedPromptTokens,
	Long totalTokens,
	BigDecimal promptCostUsd,
	BigDecimal completionCostUsd,
	BigDecimal reasoningCostUsd,
	BigDecimal cachedPromptCostUsd,
	BigDecimal totalCostUsd,
	String pricingPlanId,
	String pricingVersion,
	String sourceType,
	String metadataJson,
	LocalDateTime occurredAt
) {

	public static UsageLogResponse from(UsageLog log) {
		return new UsageLogResponse(
			log.getId(),
			log.getOrganizationId(),
			log.getProjectId(),
			log.getApiKeyId(),
			log.getEnvironment(),
			log.getRequestId(),
			log.getProvider(),
			log.getModel(),
			log.getPromptTokens(),
			log.getCompletionTokens(),
			log.getReasoningTokens(),
			log.getCachedPromptTokens(),
			log.getTotalTokens(),
			log.getPromptCostUsd(),
			log.getCompletionCostUsd(),
			log.getReasoningCostUsd(),
			log.getCachedPromptCostUsd(),
			log.getTotalCostUsd(),
			log.getPricingPlanId(),
			log.getPricingVersion(),
			log.getSourceType(),
			log.getMetadataJson(),
			log.getOccurredAt()
		);
	}
}
