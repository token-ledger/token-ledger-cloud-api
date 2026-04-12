package com.ledger.springailedger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.springailedger.dto.MemberResponse;
import com.ledger.springailedger.dto.MemberSignupRequest;
import com.ledger.springailedger.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/api/members")
	public ResponseEntity<MemberResponse> signup(@Valid @RequestBody MemberSignupRequest request) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@GetMapping("/api/me")
	public ResponseEntity<MemberResponse> me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok(memberService.getCurrentMember(authentication));
	}
}
