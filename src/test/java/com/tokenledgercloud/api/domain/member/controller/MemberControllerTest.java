package com.tokenledgercloud.api.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tokenledgercloud.api.domain.member.dto.MemberResponse;
import com.tokenledgercloud.api.global.exception.GlobalExceptionHandler;
import com.tokenledgercloud.api.domain.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

	@Mock
	private MemberService memberService;

	@InjectMocks
	private MemberController memberController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(memberController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void signupReturnsWrappedSuccessResponse() throws Exception {
		given(memberService.signup(any()))
			.willReturn(new MemberResponse("user-1", "user@test.com", "tester", "USER", "local"));

		mockMvc().perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "user@test.com",
					  "password": "password123",
					  "name": "tester"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("SUCCESS"))
			.andExpect(jsonPath("$.data.email").value("user@test.com"));
	}

	@Test
	void signupReturnsValidationErrorResponse() throws Exception {
		mockMvc().perform(post("/api/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "invalid-email",
					  "password": "1234",
					  "name": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-400"))
			.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	void meReturnsWrappedSuccessResponse() throws Exception {
		Authentication authentication = new TestingAuthenticationToken("user@test.com", "password");
		given(memberService.getCurrentMember(any(Authentication.class)))
			.willReturn(new MemberResponse("user-1", "user@test.com", "tester", "USER", "local"));

		mockMvc().perform(get("/api/me").principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.name").value("tester"));
	}
}
