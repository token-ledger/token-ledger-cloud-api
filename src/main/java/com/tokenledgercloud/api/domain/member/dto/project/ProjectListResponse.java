package com.tokenledgercloud.api.dto.project;

import java.util.List;

public record ProjectListResponse(
	List<ProjectListItemResponse> items
) {
}
