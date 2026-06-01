package com.tokenledgercloud.api.dto.project;

import com.tokenledgercloud.api.domain.project.ProjectStatus;

public record ProjectCreateResponse(
	String projectId,
	String name,
	String projectKey,
	ProjectStatus status
) {
}
