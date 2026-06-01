package com.tokenledgercloud.api.controller;

import com.tokenledgercloud.api.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.dto.ApiKeyResponse;
import com.tokenledgercloud.api.service.ApiKeyService;
import jakarta.validation.Valid;
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
    public ApiKeyCreateResponse createApiKey(Authentication authentication, @Valid @RequestBody ApiKeyCreateRequest request) {
        return apiKeyService.createApiKey(authentication, request);
    }

    @GetMapping
    public List<ApiKeyResponse> getMyApiKeys(Authentication authentication) {
        return apiKeyService.getMyApiKeys(authentication);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateApiKey(Authentication authentication, @PathVariable Long id) {
        apiKeyService.deactivateApiKey(authentication, id);
    }

    @DeleteMapping("/{id}")
    public void deleteApiKey(Authentication authentication, @PathVariable Long id) {
        apiKeyService.deleteApiKey(authentication, id);
    }
}
