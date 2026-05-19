package com.tokenledgercloud.api.domain.event.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.event.dto.OperationalEventItemResponse;
import com.tokenledgercloud.api.domain.event.dto.OperationalEventListResponse;
import com.tokenledgercloud.api.domain.event.entity.OperationalEvent;
import com.tokenledgercloud.api.domain.event.repository.OperationalEventRepository;
import com.tokenledgercloud.api.global.exception.ApiException;
import com.tokenledgercloud.api.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperationalEventService {

	private final OperationalEventRepository operationalEventRepository;

	@Transactional(readOnly = true)
	public OperationalEventListResponse getEvents(
		String projectId,
		String environment,
		String type,
		String cursor,
		Integer size
	) {
		String normalizedType = normalizeType(type);
		int limit = size == null ? 20 : Math.min(size, 100);

		LocalDateTime cursorOccurredAt = null;

		if (cursor != null && !cursor.isBlank()) {
			OperationalEvent cursorEvent = operationalEventRepository.findById(cursor)
				.orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

			cursorOccurredAt = cursorEvent.getOccurredAt();
		}

		List<OperationalEvent> events = operationalEventRepository.findEvents(
			blankToNull(projectId),
			blankToNull(environment),
			normalizedType,
			cursorOccurredAt,
			PageRequest.of(0, limit)
		);

		List<OperationalEventItemResponse> items = events.stream()
			.map(OperationalEventItemResponse::from)
			.toList();

		String nextCursor = items.isEmpty()
			? null
			: items.get(items.size() - 1).id();

		return new OperationalEventListResponse(items, nextCursor);
	}

	private String normalizeType(String type) {
		if (type == null || type.isBlank()) {
			return null;
		}

		String normalizedType = type.toUpperCase();

		if (!normalizedType.equals("INFO")
			&& !normalizedType.equals("WARNING")
			&& !normalizedType.equals("CRITICAL")) {
			throw new ApiException(ErrorCode.INVALID_EVENT_TYPE);
		}

		return normalizedType;
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}
}