package com.tokenledgercloud.api.domain.apikey.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tokenledgercloud.api.domain.apikey.ApiKey;
import com.tokenledgercloud.api.domain.apikey.ApiKeyRepository;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.domain.member.entity.Member;
import com.tokenledgercloud.api.domain.member.entity.Role;
import com.tokenledgercloud.api.domain.member.repository.MemberRepository;
import com.tokenledgercloud.api.global.util.HashingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id("member-1")
                .email("user@test.com")
                .name("tester")
                .role(Role.USER)
                .provider("local")
                .build();
    }

    @Test
    void createApiKeyStoresHashAndReturnsRawKeyOnce() {
        given(memberRepository.findByEmail("user@test.com")).willReturn(Optional.of(member));
        given(apiKeyRepository.countByMemberId("member-1")).willReturn(0L);

        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setName("server key");

        ApiKeyCreateResponse response = apiKeyService.createApiKey(
                new TestingAuthenticationToken("user@test.com", null),
                request
        );

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());

        ApiKey savedApiKey = captor.getValue();
        assertThat(response.getRawKey()).startsWith("tk-");
        assertThat(savedApiKey.getHashedKey()).isEqualTo(HashingUtil.sha256(response.getRawKey()));
        assertThat(savedApiKey.getDisplayKey()).isEqualTo(response.getDisplayKey());
        assertThat(savedApiKey.getHashedKey()).isNotEqualTo(response.getRawKey());
    }

    @Test
    void createApiKeyRejectsWhenMemberAlreadyHasMaxKeys() {
        given(memberRepository.findByEmail("user@test.com")).willReturn(Optional.of(member));
        given(apiKeyRepository.countByMemberId("member-1")).willReturn(5L);

        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setName("overflow key");

        assertThatThrownBy(() -> apiKeyService.createApiKey(new TestingAuthenticationToken("user@test.com", null), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Maximum number of API keys reached");
    }

    @Test
    void verifyApiKeyFindsActiveHashAndUpdatesLastUsedAt() {
        String rawKey = "tk-1234567890abcdef";
        ApiKey apiKey = ApiKey.builder()
                .hashedKey(HashingUtil.sha256(rawKey))
                .displayKey("tk-123...cdef")
                .member(member)
                .isActive(true)
                .build();

        given(apiKeyRepository.findByHashedKeyAndIsActiveTrue(HashingUtil.sha256(rawKey)))
                .willReturn(Optional.of(apiKey));

        Member authenticatedMember = apiKeyService.verifyApiKey(rawKey);

        assertThat(authenticatedMember).isEqualTo(member);
        assertThat(apiKey.getLastUsedAt()).isNotNull();
    }

    @Test
    void verifyApiKeyRejectsInvalidKey() {
        String rawKey = "tk-invalid";
        given(apiKeyRepository.findByHashedKeyAndIsActiveTrue(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> apiKeyService.verifyApiKey(rawKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or inactive API Key");
    }
}
