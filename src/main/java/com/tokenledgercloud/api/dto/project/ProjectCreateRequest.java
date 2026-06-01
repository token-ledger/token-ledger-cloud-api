package com.tokenledgercloud.api.dto.project;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
	@NotBlank @Size(max = 100) String name,
	@NotBlank @Size(max = 100) String projectKey,
	@NotEmpty List<@NotBlank @Size(max = 50) String> environments,
	@NotBlank @Size(max = 100) String defaultModel
) {
}
