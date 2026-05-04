package com.tokenledgercloud.api.dto.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tokenledgercloud.api.domain.usage.UsageStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UsageLogCreateRequest(
	@NotBlank String eventId,
	@NotBlank String idempotencyKey,

	Long projectId,
	Long applicationId,
	Long userId,

	@NotBlank String modelId,

	@NotNull @PositiveOrZero Long inputTokens,
	@NotNull @PositiveOrZero Long outputTokens,

	Long totalTokens,

	@NotNull @DecimalMin("0.00000000") BigDecimal totalCost,

	String currencyCode,

	@NotNull UsageStatus status,

	@NotNull LocalDateTime startedAt,
	LocalDateTime finishedAt,

	Long latencyMs,
	String errorCode,
	String errorMessage
) {
}