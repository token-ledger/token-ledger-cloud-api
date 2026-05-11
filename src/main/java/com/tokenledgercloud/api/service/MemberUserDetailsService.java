package com.tokenledgercloud.api.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username)
			.or(() -> memberRepository.findByProviderId(username))
			.orElseThrow(() -> new UsernameNotFoundException(username));

		// 소셜 로그인 사용자는 password 가 없으므로, 폼 로그인으로는 매칭되지 않는 placeholder 를 사용한다.
		// JWT 인증 흐름에서는 password 검증을 거치지 않으므로 UserDetails 만 정상적으로 반환되면 된다.
		String password = (member.getPassword() == null || member.getPassword().isBlank())
			? "{noop}N/A_SOCIAL_LOGIN"
			: member.getPassword();

		return User.builder()
			.username(member.getEmail() != null ? member.getEmail() : member.getProviderId())
			.password(password)
			.roles(member.getRole().name())
			.build();
	}
}
