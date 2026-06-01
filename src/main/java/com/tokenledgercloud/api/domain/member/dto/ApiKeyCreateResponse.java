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
public class ApiKeyCreateResponse {
    private Long id;
    private String rawKey;
    private String displayKey;
    private String name;
    private LocalDateTime createdAt;
    private boolean active;
}
