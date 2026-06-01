package com.tokenledgercloud.api.domain.apikey.dto;

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
    private String id;
    private String displayKey;
    private String name;
    private LocalDateTime createdAt;
    private boolean isActive;
}
