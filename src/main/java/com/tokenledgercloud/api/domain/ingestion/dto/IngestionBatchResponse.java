package com.tokenledgercloud.api.domain.ingestion.dto;

import java.util.List;

public record IngestionBatchResponse(
	int acceptedCount,
	int rejectedCount,
	List<RejectedIngestionItemResponse> rejectedItems
) {
}
