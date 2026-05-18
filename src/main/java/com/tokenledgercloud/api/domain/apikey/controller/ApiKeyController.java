package com.tokenledgercloud.api.domain.apikey.controller;

import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyResponse;
import com.tokenledgercloud.api.domain.apikey.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ApiKeyResponse createApiKey(Authentication authentication, @RequestBody ApiKeyCreateRequest request) {
        return apiKeyService.createApiKey(authentication, request);
    }

    @GetMapping
    public List<ApiKeyResponse> getMyApiKeys(Authentication authentication) {
        return apiKeyService.getMyApiKeys(authentication);
    }

    @DeleteMapping("/{id}")
    public void deleteApiKey(Authentication authentication, @PathVariable String id) {
        apiKeyService.deleteApiKey(authentication, id);
    }
}
