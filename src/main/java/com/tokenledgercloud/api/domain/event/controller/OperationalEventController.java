package com.tokenledgercloud.api.domain.event.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.event.dto.OperationalEventListResponse;
import com.tokenledgercloud.api.domain.event.service.OperationalEventService;
import com.tokenledgercloud.api.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class OperationalEventController {

	private final OperationalEventService operationalEventService;

	@GetMapping
	public ResponseEntity<ApiResponse<OperationalEventListResponse>> getEvents(
		@RequestParam(required = false) String projectId,
		@RequestParam(required = false) String environment,
		@RequestParam(required = false) String type,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"운영 이벤트 목록 조회 성공",
				operationalEventService.getEvents(projectId, environment, type, cursor, size)
			)
		);
	}
}