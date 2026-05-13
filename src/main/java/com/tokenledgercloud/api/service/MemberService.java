package com.tokenledgercloud.api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.domain.member.Role;
import com.tokenledgercloud.api.dto.MemberResponse;
import com.tokenledgercloud.api.dto.MemberSignupRequest;
import com.tokenledgercloud.api.global.exception.DuplicateMemberEmailException;
import com.tokenledgercloud.api.global.exception.MemberNotFoundException;
import com.tokenledgercloud.api.global.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public MemberResponse signup(MemberSignupRequest request) {
		if (memberRepository.findByEmail(request.email()).isPresent()) {
			throw new DuplicateMemberEmailException();
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
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthorizedException();
		}

		String identifier = authentication.getName();
		return memberRepository.findByEmail(identifier)
			.or(() -> memberRepository.findByProviderId(identifier))
			.map(MemberResponse::from)
			.orElseThrow(MemberNotFoundException::new);
	}
}
