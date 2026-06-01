package com.tokenledgercloud.api.service;

import com.tokenledgercloud.api.domain.apikey.ApiKey;
import com.tokenledgercloud.api.domain.apikey.ApiKeyRepository;
import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.dto.ApiKeyResponse;
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

        long currentKeysCount = apiKeyRepository.findByMemberId(member.getId()).size();
        if (currentKeysCount >= MAX_API_KEYS_PER_MEMBER) {
            throw new IllegalStateException("Maximum number of API keys reached (" + MAX_API_KEYS_PER_MEMBER + ")");
        }

        String rawKey = "tk-" + UUID.randomUUID().toString().replace("-", "");
        String hashedKey = HashingUtil.hash(rawKey);
        String displayKey = rawKey.substring(0, 7) + "..." + rawKey.substring(rawKey.length() - 4);

        ApiKey apiKey = ApiKey.builder()
                .hashedKey(hashedKey)
                .displayKey(displayKey)
                .member(member)
                .name(request.getName())
                .active(true)
                .build();

        apiKeyRepository.save(apiKey);

        return toCreateResponse(apiKey, rawKey);
    }

    @Transactional
    public Member verifyApiKey(String rawKey) {
        String hashedKey = HashingUtil.hash(rawKey);
        ApiKey apiKey = apiKeyRepository.findByHashedKeyAndActiveTrue(hashedKey)
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
    public void deactivateApiKey(Authentication authentication, Long apiKeyId) {
        ApiKey apiKey = getMyApiKey(authentication, apiKeyId);
        apiKey.setActive(false);
    }

    @Transactional
    public void deleteApiKey(Authentication authentication, Long apiKeyId) {
        ApiKey apiKey = getMyApiKey(authentication, apiKeyId);
        apiKeyRepository.delete(apiKey);
    }

    private ApiKey getMyApiKey(Authentication authentication, Long apiKeyId) {
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
                .active(apiKey.isActive())
                .build();
    }

    private ApiKeyCreateResponse toCreateResponse(ApiKey apiKey, String rawKey) {
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .rawKey(rawKey)
                .displayKey(apiKey.getDisplayKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt())
                .active(apiKey.isActive())
                .build();
    }
}
