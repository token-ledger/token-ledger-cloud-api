package com.tokenledgercloud.api.domain.event.dto;

import java.time.LocalDateTime;

import com.tokenledgercloud.api.domain.event.entity.OperationalEvent;

public record OperationalEventItemResponse(
	String id,
	LocalDateTime time,
	String projectId,
	String projectName,
	String environment,
	String type,
	String message
) {
	public static OperationalEventItemResponse from(OperationalEvent event) {
		return new OperationalEventItemResponse(
			event.getId(),
			event.getOccurredAt(),
			event.getProjectId(),
			event.getProjectId(),
			event.getEnvironment(),
			event.getEventType(),
			event.getMessage()
		);
	}
}