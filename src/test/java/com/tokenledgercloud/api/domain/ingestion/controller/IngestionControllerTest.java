package com.tokenledgercloud.api.domain.ingestion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tokenledgercloud.api.domain.ingestion.dto.IngestionBatchResponse;
import com.tokenledgercloud.api.domain.ingestion.dto.IngestionEventResponse;
import com.tokenledgercloud.api.domain.ingestion.dto.RejectedIngestionItemResponse;
import com.tokenledgercloud.api.domain.ingestion.service.IngestionService;
import com.tokenledgercloud.api.global.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class IngestionControllerTest {

	@Mock
	private IngestionService ingestionService;

	@InjectMocks
	private IngestionController ingestionController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(ingestionController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void collectEventReturnsWrappedSuccessResponse() throws Exception {
		given(ingestionService.collectEvent(eq("test-api-key"), any()))
			.willReturn(new IngestionEventResponse("evt_ing_001", true));

		mockMvc().perform(post("/api/ingestion/events")
				.header("X-API-Key", "test-api-key")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "projectKey": "support-copilot",
					  "environment": "prod",
					  "requestId": "req_123",
					  "provider": "openai",
					  "model": "gpt-4o-mini",
					  "promptTokens": 1200,
					  "completionTokens": 400,
					  "reasoningTokens": 0,
					  "totalTokens": 1600,
					  "promptCostUsd": 0.00018,
					  "completionCostUsd": 0.00024,
					  "reasoningCostUsd": 0,
					  "totalCostUsd": 0.00042,
					  "pricingVersion": "2026-05-01",
					  "occurredAt": "2026-05-06T10:00:00Z",
					  "metadata": {
					    "tenantId": "tenant-a"
					  }
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("사용 이벤트 수집 성공"))
			.andExpect(jsonPath("$.data.eventId").value("evt_ing_001"))
			.andExpect(jsonPath("$.data.accepted").value(true));
	}

	@Test
	void collectEventReturnsValidationErrorResponse() throws Exception {
		mockMvc().perform(post("/api/ingestion/events")
				.header("X-API-Key", "test-api-key")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "projectKey": "support-copilot",
					  "environment": "prod",
					  "provider": "openai"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-400"));
	}

	@Test
	void collectBatchReturnsWrappedSuccessResponse() throws Exception {
		given(ingestionService.collectBatch(eq("test-api-key"), any()))
			.willReturn(new IngestionBatchResponse(
				1,
				1,
				List.of(new RejectedIngestionItemResponse(1, "req_bad", "COMMON-400", "Invalid request input."))
			));

		mockMvc().perform(post("/api/ingestion/events/batch")
				.header("X-API-Key", "test-api-key")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "projectKey": "support-copilot",
					  "environment": "prod",
					  "items": [
					    {
					      "requestId": "req_123",
					      "provider": "openai",
					      "model": "gpt-4o-mini",
					      "promptTokens": 1200,
					      "completionTokens": 400,
					      "totalTokens": 1600,
					      "totalCostUsd": 0.00042,
					      "pricingVersion": "2026-05-01",
					      "occurredAt": "2026-05-06T10:00:00Z"
					    }
					  ]
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("배치 수집 성공"))
			.andExpect(jsonPath("$.data.acceptedCount").value(1))
			.andExpect(jsonPath("$.data.rejectedCount").value(1))
			.andExpect(jsonPath("$.data.rejectedItems[0].requestId").value("req_bad"));
	}
}
