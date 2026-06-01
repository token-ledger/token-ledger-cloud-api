package com.tokenledgercloud.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenledgercloud.api.dto.ApiKeyCreateRequest;
import com.tokenledgercloud.api.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.dto.ApiKeyResponse;
import com.tokenledgercloud.api.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApiKeyService apiKeyService;

    @InjectMocks
    private ApiKeyController apiKeyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiKeyController).build();
    }

    @Test
    @DisplayName("API 키 생성 API 호출 성공")
    void createApiKey_ApiSuccess() throws Exception {
        // given
        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setName("New Key");

        ApiKeyCreateResponse response = ApiKeyCreateResponse.builder()
                .id(1L)
                .rawKey("tk-123456789")
                .displayKey("tk-123...6789")
                .name("New Key")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        given(apiKeyService.createApiKey(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawKey").value("tk-123456789"))
                .andExpect(jsonPath("$.displayKey").value("tk-123...6789"));
    }

    @Test
    @DisplayName("내 API 키 목록 조회 API 호출 성공")
    void getMyApiKeys_ApiSuccess() throws Exception {
        // given
        ApiKeyResponse response = ApiKeyResponse.builder()
                .id(1L)
                .displayKey("tk-abc...def")
                .name("My Key")
                .active(true)
                .build();

        given(apiKeyService.getMyApiKeys(any())).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/api-keys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].displayKey").value("tk-abc...def"))
                .andExpect(jsonPath("$[0].name").value("My Key"));
    }
}
