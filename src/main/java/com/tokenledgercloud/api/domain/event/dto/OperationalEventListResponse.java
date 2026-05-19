package com.tokenledgercloud.api.domain.event.dto;

import java.util.List;

public record OperationalEventListResponse(
	List<OperationalEventItemResponse> items,
	String nextCursor
) {
}