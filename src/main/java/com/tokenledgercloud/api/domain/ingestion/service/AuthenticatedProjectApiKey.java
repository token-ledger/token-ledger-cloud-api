package com.tokenledgercloud.api.domain.ingestion.service;

public record AuthenticatedProjectApiKey(
	String organizationId,
	String projectId,
	String apiKeyId,
	String environment
) {
}
