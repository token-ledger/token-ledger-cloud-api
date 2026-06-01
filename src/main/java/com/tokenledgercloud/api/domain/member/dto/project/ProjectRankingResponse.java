package com.tokenledgercloud.api.dto.project;

import java.util.List;

public record ProjectRankingResponse(
	List<ProjectRankingItemResponse> items
) {
}
