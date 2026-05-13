package com.tokenledgercloud.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyResponse {
    private Long id;
    private String apiKey;
    private String name;
    private LocalDateTime createdAt;
    private boolean isActive;
}
