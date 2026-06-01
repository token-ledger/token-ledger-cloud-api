package com.tokenledgercloud.api.domain.ingestion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tokenledgercloud.api.domain.ingestion.dto.IngestionBatchRequest;
import com.tokenledgercloud.api.domain.ingestion.dto.IngestionBatchResponse;
import com.tokenledgercloud.api.domain.ingestion.dto.IngestionEventRequest;
import com.tokenledgercloud.api.domain.ingestion.dto.IngestionEventResponse;
import com.tokenledgercloud.api.domain.ingestion.service.IngestionService;
import com.tokenledgercloud.api.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingestion/events")
@RequiredArgsConstructor
public class IngestionController {

	private static final String API_KEY_HEADER = "X-API-Key";

	private final IngestionService ingestionService;

	@PostMapping
	public ResponseEntity<ApiResponse<IngestionEventResponse>> collectEvent(
		@RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
		@Valid @RequestBody IngestionEventRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("사용 이벤트 수집 성공", ingestionService.collectEvent(apiKey, request)));
	}

	@PostMapping("/batch")
	public ResponseEntity<ApiResponse<IngestionBatchResponse>> collectBatch(
		@RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
		@Valid @RequestBody IngestionBatchRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.success("배치 수집 성공", ingestionService.collectBatch(apiKey, request))
		);
	}
}
