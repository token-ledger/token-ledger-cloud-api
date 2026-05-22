package com.tokenledgercloud.api.domain.apikey.service;

import com.tokenledgercloud.api.domain.apikey.ApiKey;
import com.tokenledgercloud.api.domain.apikey.ApiKeyRepository;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.domain.member.entity.Member;
import com.tokenledgercloud.api.domain.member.repository.MemberRepository;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyResponse;
import com.tokenledgercloud.api.global.util.HashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MemberRepository memberRepository;
    private static final int MAX_API_KEYS_PER_MEMBER = 5;

    @Transactional
    public ApiKeyCreateResponse createApiKey(Authentication authentication, ApiKeyCreateRequest request) {
        Member member = getMember(authentication);

        long apiKeyCount = apiKeyRepository.countByMemberId(member.getId());
        if (apiKeyCount >= MAX_API_KEYS_PER_MEMBER) {
            throw new IllegalStateException("Maximum number of API keys reached (" + MAX_API_KEYS_PER_MEMBER + ")");
        }

        String rawKey = "tk-" + UUID.randomUUID().toString().replace("-", "");

        ApiKey apiKey = ApiKey.builder()
                .hashedKey(HashingUtil.sha256(rawKey))
                .displayKey(createDisplayKey(rawKey))
                .member(member)
                .name(request.getName())
                .isActive(true)
                .build();

        apiKeyRepository.save(apiKey);

        return toCreateResponse(apiKey, rawKey);
    }

    @Transactional
    public Member verifyApiKey(String rawKey) {
        ApiKey apiKey = apiKeyRepository.findByHashedKeyAndIsActiveTrue(HashingUtil.sha256(rawKey))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive API Key"));

        apiKey.setLastUsedAt(LocalDateTime.now());
        return apiKey.getMember();
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getMyApiKeys(Authentication authentication) {
        Member member = getMember(authentication);
        return apiKeyRepository.findByMemberId(member.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteApiKey(Authentication authentication, String apiKeyId) {
        ApiKey apiKey = getMyApiKey(authentication, apiKeyId);
        apiKeyRepository.delete(apiKey);
    }

    @Transactional
    public void deactivateApiKey(Authentication authentication, String apiKeyId) {
        ApiKey apiKey = getMyApiKey(authentication, apiKeyId);
        apiKey.setActive(false);
    }

    private ApiKey getMyApiKey(Authentication authentication, String apiKeyId) {
        Member member = getMember(authentication);
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new IllegalArgumentException("API Key not found"));

        if (!apiKey.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized to access this API Key");
        }

        return apiKey;
    }

    private Member getMember(Authentication authentication) {
        String identifier = authentication.getName();
        return memberRepository.findByEmail(identifier)
                .or(() -> memberRepository.findByProviderId(identifier))
                .orElseThrow(() -> new IllegalStateException("Member not found"));
    }

    private ApiKeyResponse toResponse(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .displayKey(apiKey.getDisplayKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt())
                .isActive(apiKey.isActive())
                .build();
    }

    private ApiKeyCreateResponse toCreateResponse(ApiKey apiKey, String rawKey) {
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .rawKey(rawKey)
                .displayKey(apiKey.getDisplayKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt())
                .isActive(apiKey.isActive())
                .build();
    }

    private String createDisplayKey(String rawKey) {
        return rawKey.substring(0, 7) + "..." + rawKey.substring(rawKey.length() - 4);
    }
}
