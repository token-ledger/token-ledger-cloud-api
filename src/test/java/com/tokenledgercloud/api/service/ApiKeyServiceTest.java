package com.tokenledgercloud.api.service;

import com.tokenledgercloud.api.domain.apikey.ApiKey;
import com.tokenledgercloud.api.domain.apikey.ApiKeyRepository;
import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.domain.member.Role;
import com.tokenledgercloud.api.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.global.util.HashingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .email("test@example.com")
                .name("Tester")
                .role(Role.USER)
                .provider("google")
                .build();
    }

    @Test
    @DisplayName("API 키 생성 성공")
    void createApiKey_Success() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn("test@example.com");
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(testMember));
        given(apiKeyRepository.findByMemberId(testMember.getId())).willReturn(new ArrayList<>());

        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setName("My Test Key");

        // when
        ApiKeyCreateResponse response = apiKeyService.createApiKey(authentication, request);

        // then
        assertThat(response.getRawKey()).startsWith("tk-");
        assertThat(response.getDisplayKey()).contains("...");
        assertThat(response.getName()).isEqualTo("My Test Key");
        verify(apiKeyRepository).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("API 키 생성 실패 - 최대 개수(5개) 초과")
    void createApiKey_LimitExceeded() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn("test@example.com");
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(testMember));
        
        List<ApiKey> existingKeys = new ArrayList<>();
        for (int i = 0; i < 5; i++) existingKeys.add(new ApiKey());
        given(apiKeyRepository.findByMemberId(testMember.getId())).willReturn(existingKeys);

        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setName("Sixth Key");

        // when & then
        assertThatThrownBy(() -> apiKeyService.createApiKey(authentication, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Maximum number of API keys reached");
    }

    @Test
    @DisplayName("API 키 검증 성공")
    void verifyApiKey_Success() {
        // given
        String rawKey = "tk-valid-key-example-123456789";
        String hashedKey = HashingUtil.hash(rawKey);
        ApiKey apiKey = ApiKey.builder()
                .hashedKey(hashedKey)
                .member(testMember)
                .active(true)
                .build();

        given(apiKeyRepository.findByHashedKeyAndActiveTrue(hashedKey)).willReturn(Optional.of(apiKey));

        // when
        Member result = apiKeyService.verifyApiKey(rawKey);

        // then
        assertThat(result.getEmail()).isEqualTo(testMember.getEmail());
        assertThat(apiKey.getLastUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("API 키 검증 실패 - 잘못된 키")
    void verifyApiKey_Invalid() {
        // given
        String rawKey = "tk-wrong-key";
        String hashedKey = HashingUtil.hash(rawKey);
        given(apiKeyRepository.findByHashedKeyAndActiveTrue(hashedKey)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> apiKeyService.verifyApiKey(rawKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or inactive API Key");
    }
}
