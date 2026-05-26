package com.tokenledgercloud.api.domain.ingestion.dto;

public record RejectedIngestionItemResponse(
	int index,
	String requestId,
	String code,
	String message
) {
}
