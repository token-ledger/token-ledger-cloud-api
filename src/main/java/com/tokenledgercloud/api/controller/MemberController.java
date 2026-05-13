package com.tokenledgercloud.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tokenledgercloud.api.dto.MemberResponse;
import com.tokenledgercloud.api.dto.MemberSignupRequest;
import com.tokenledgercloud.api.global.response.ApiResponse;
import com.tokenledgercloud.api.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/api/members")
	public ResponseEntity<ApiResponse<MemberResponse>> signup(@Valid @RequestBody MemberSignupRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Member created successfully.", memberService.signup(request)));
	}

	@GetMapping("/api/me")
	public ResponseEntity<ApiResponse<MemberResponse>> me(Authentication authentication) {
		return ResponseEntity.ok(ApiResponse.success(memberService.getCurrentMember(authentication)));
	}
}
