package com.tokenledgercloud.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.dto.usage.UsageLogCreateRequest;
import com.tokenledgercloud.api.dto.usage.UsageLogResponse;
import com.tokenledgercloud.api.service.UsageLogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsageLogController {

	private final UsageLogService usageLogService;

	@PostMapping("/internal/usage-logs")
	public ResponseEntity<UsageLogResponse> create(@Valid @RequestBody UsageLogCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usageLogService.create(request));
	}
}