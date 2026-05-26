package com.tokenledgercloud.api.domain.ingestion.dto;

public record IngestionEventResponse(
	String eventId,
	boolean accepted
) {
}
