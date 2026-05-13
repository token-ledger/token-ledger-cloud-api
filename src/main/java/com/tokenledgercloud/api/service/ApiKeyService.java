package com.tokenledgercloud.api.service;

import com.tokenledgercloud.api.domain.apikey.ApiKey;
import com.tokenledgercloud.api.domain.apikey.ApiKeyRepository;
import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.dto.ApiKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ApiKeyResponse createApiKey(Authentication authentication, ApiKeyCreateRequest request) {
        Member member = getMember(authentication);

        String generatedKey = "tk-" + UUID.randomUUID().toString().replace("-", "");

        ApiKey apiKey = ApiKey.builder()
                .apiKey(generatedKey)
                .member(member)
                .name(request.getName())
                .isActive(true)
                .build();

        apiKeyRepository.save(apiKey);

        return toResponse(apiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getMyApiKeys(Authentication authentication) {
        Member member = getMember(authentication);
        return apiKeyRepository.findByMemberId(member.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteApiKey(Authentication authentication, Long apiKeyId) {
        Member member = getMember(authentication);
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new IllegalArgumentException("API Key not found"));

        if (!apiKey.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this API Key");
        }

        apiKeyRepository.delete(apiKey);
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
                .apiKey(apiKey.getApiKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt())
                .isActive(apiKey.isActive())
                .build();
    }
}
