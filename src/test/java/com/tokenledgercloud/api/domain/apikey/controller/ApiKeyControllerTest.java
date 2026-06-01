package com.tokenledgercloud.api.domain.apikey.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tokenledgercloud.api.domain.apikey.dto.ApiKeyCreateResponse;
import com.tokenledgercloud.api.domain.apikey.service.ApiKeyService;
import com.tokenledgercloud.api.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerTest {

    @Mock
    private ApiKeyService apiKeyService;

    @InjectMocks
    private ApiKeyController apiKeyController;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(apiKeyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createApiKeyReturnsRawKeyOnlyOnCreateResponse() throws Exception {
        given(apiKeyService.createApiKey(any(), any()))
                .willReturn(ApiKeyCreateResponse.builder()
                        .id("api-key-1")
                        .rawKey("tk-1234567890abcdef")
                        .displayKey("tk-123...cdef")
                        .name("server key")
                        .isActive(true)
                        .build());

        mockMvc().perform(post("/api/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "server key"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawKey").value("tk-1234567890abcdef"))
                .andExpect(jsonPath("$.displayKey").value("tk-123...cdef"));
    }

    @Test
    void createApiKeyRejectsBlankName() throws Exception {
        mockMvc().perform(post("/api/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-400"));
    }
}
