package com.tokenledgercloud.api.domain.usage.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.usage.dto.UsageEventItemResponse;
import com.tokenledgercloud.api.domain.usage.dto.UsageEventListResponse;
import com.tokenledgercloud.api.domain.usage.dto.UsageLogCreateRequest;
import com.tokenledgercloud.api.domain.usage.dto.UsageLogResponse;
import com.tokenledgercloud.api.domain.usage.entity.UsageLog;
import com.tokenledgercloud.api.domain.usage.repository.UsageLogRepository;
import com.tokenledgercloud.api.global.exception.ApiException;
import com.tokenledgercloud.api.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageLogService {

	private final UsageLogRepository usageLogRepository;

	@Transactional
	public UsageLogResponse create(UsageLogCreateRequest request) {
		if (request.requestId() != null && !request.requestId().isBlank()) {
			return usageLogRepository.findByProjectIdAndEnvironmentAndRequestId(
					request.projectId(),
					request.environment(),
					request.requestId()
				)
				.map(UsageLogResponse::from)
				.orElseGet(() -> UsageLogResponse.from(saveNew(request)));
		}

		return UsageLogResponse.from(saveNew(request));
	}

	@Transactional(readOnly = true)
	public UsageEventListResponse getRecentEvents(
		String projectId,
		String environment,
		String provider,
		String model,
		String cursor,
		Integer size
	) {
		int limit = size == null ? 20 : Math.min(size, 100);

		LocalDateTime cursorOccurredAt = null;

		if (cursor != null && !cursor.isBlank()) {
			UsageLog cursorLog = usageLogRepository.findById(cursor)
				.orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

			cursorOccurredAt = cursorLog.getOccurredAt();
		}

		List<UsageLog> logs = usageLogRepository.findRecentUsageEvents(
			blankToNull(projectId),
			blankToNull(environment),
			blankToNull(provider),
			blankToNull(model),
			cursorOccurredAt,
			PageRequest.of(0, limit)
		);

		List<UsageEventItemResponse> items = logs.stream()
			.map(UsageEventItemResponse::from)
			.toList();

		String nextCursor = items.isEmpty()
			? null
			: items.get(items.size() - 1).eventId();

		return new UsageEventListResponse(items, nextCursor);
	}

	private UsageLog saveNew(UsageLogCreateRequest request) {
		Long totalTokens = request.totalTokens() != null
			? request.totalTokens()
			: request.promptTokens()
				+ request.completionTokens()
				+ safe(request.reasoningTokens())
				+ safe(request.cachedPromptTokens());

		var totalCostUsd = request.totalCostUsd() != null
			? request.totalCostUsd()
			: request.promptCostUsd()
				.add(request.completionCostUsd())
				.add(safe(request.reasoningCostUsd()))
				.add(safe(request.cachedPromptCostUsd()));

		UsageLog usageLog = UsageLog.builder()
			.organizationId(request.organizationId())
			.projectId(request.projectId())
			.apiKeyId(request.apiKeyId())
			.environment(request.environment())
			.requestId(request.requestId())
			.provider(request.provider())
			.model(request.model())
			.promptTokens(request.promptTokens())
			.completionTokens(request.completionTokens())
			.reasoningTokens(safe(request.reasoningTokens()))
			.cachedPromptTokens(safe(request.cachedPromptTokens()))
			.totalTokens(totalTokens)
			.promptCostUsd(request.promptCostUsd())
			.completionCostUsd(request.completionCostUsd())
			.reasoningCostUsd(safe(request.reasoningCostUsd()))
			.cachedPromptCostUsd(safe(request.cachedPromptCostUsd()))
			.totalCostUsd(totalCostUsd)
			.pricingPlanId(request.pricingPlanId())
			.pricingVersion(request.pricingVersion())
			.sourceType(request.sourceType())
			.metadataJson(request.metadataJson())
			.occurredAt(request.occurredAt())
			.build();

		return usageLogRepository.save(usageLog);
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private Long safe(Long value) {
		return value == null ? 0L : value;
	}

	private java.math.BigDecimal safe(java.math.BigDecimal value) {
		return value == null ? java.math.BigDecimal.ZERO : value;
	}
}