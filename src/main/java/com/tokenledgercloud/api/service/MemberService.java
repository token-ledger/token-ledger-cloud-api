package com.tokenledgercloud.api.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.domain.member.Role;
import com.tokenledgercloud.api.dto.MemberResponse;
import com.tokenledgercloud.api.dto.MemberSignupRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public MemberResponse signup(MemberSignupRequest request) {
		if (memberRepository.findByEmail(request.email()).isPresent()) {
			throw new IllegalArgumentException("Email already registered");
		}
		Member member = Member.builder()
			.email(request.email())
			.name(request.name())
			.password(passwordEncoder.encode(request.password()))
			.role(Role.USER)
			.provider("local")
			.providerId(null)
			.build();
		memberRepository.save(member);
		return MemberResponse.from(member);
	}

	@Transactional(readOnly = true)
	public MemberResponse getCurrentMember(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			OAuth2User principal = oauthToken.getPrincipal();
			String email = principal.getAttribute("email");
			return memberRepository.findByEmail(email)
				.map(MemberResponse::from)
				.orElseThrow(() -> new IllegalStateException("OAuth member not found"));
		}
		if (authentication instanceof UsernamePasswordAuthenticationToken
			&& authentication.getPrincipal() instanceof UserDetails userDetails) {
			return memberRepository.findByEmail(userDetails.getUsername())
				.map(MemberResponse::from)
				.orElseThrow(() -> new IllegalStateException("Member not found"));
		}
		throw new IllegalStateException("Unsupported authentication type");
	}
}
