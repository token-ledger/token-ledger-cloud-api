package com.tokenledgercloud.api.domain.usage.dto;

import java.util.List;

public record UsageEventListResponse(
	List<UsageEventItemResponse> items,
	String nextCursor
) {
}