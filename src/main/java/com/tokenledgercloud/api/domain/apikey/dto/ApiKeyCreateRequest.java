package com.tokenledgercloud.api.domain.apikey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiKeyCreateRequest {
    private String name;
}
