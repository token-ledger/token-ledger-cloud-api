package com.tokenledgercloud.api.dto.project;

import java.util.List;

import com.tokenledgercloud.api.domain.project.ProjectStatus;

public record ProjectListItemResponse(
	String projectId,
	String projectName,
	List<String> environments,
	String defaultModel,
	ProjectStatus status
) {
}
