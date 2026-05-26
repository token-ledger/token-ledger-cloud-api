package com.tokenledgercloud.api.domain.ingestion.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record IngestionBatchRequest(
	@NotBlank String projectKey,
	@NotBlank String environment,
	@NotEmpty @Size(max = 100) List<IngestionEventItemRequest> items
) {
}
