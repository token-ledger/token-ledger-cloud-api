package com.tokenledgercloud.api.domain.apikey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiKeyCreateRequest {
    @NotBlank(message = "API Key name is required")
    @Size(max = 50, message = "API Key name must be less than 50 characters")
    private String name;
}
