package com.tokenledgercloud.api.domain.usage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.usage.dto.UsageEventListResponse;
import com.tokenledgercloud.api.domain.usage.dto.UsageLogCreateRequest;
import com.tokenledgercloud.api.domain.usage.dto.UsageLogResponse;
import com.tokenledgercloud.api.domain.usage.service.UsageLogService;
import com.tokenledgercloud.api.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsageLogController {

	private final UsageLogService usageLogService;

	@PostMapping("/internal/usage-logs")
	public ResponseEntity<ApiResponse<UsageLogResponse>> create(@Valid @RequestBody UsageLogCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Usage log created successfully.", usageLogService.create(request)));
	}

	@GetMapping("/api/usage-events")
	public ResponseEntity<ApiResponse<UsageEventListResponse>> getRecentEvents(
		@RequestParam(required = false) String projectId,
		@RequestParam(required = false) String environment,
		@RequestParam(required = false) String provider,
		@RequestParam(required = false) String model,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		return ResponseEntity.ok(
			ApiResponse.success(
				"최근 사용 이벤트 조회 성공",
				usageLogService.getRecentEvents(projectId, environment, provider, model, cursor, size)
			)
		);
	}
}