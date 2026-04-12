package com.ledger.springailedger.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ledger.springailedger.domain.member.Member;
import com.ledger.springailedger.domain.member.MemberRepository;
import com.ledger.springailedger.domain.member.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String providerId = oauth2User.getAttribute("sub");
		String email = oauth2User.getAttribute("email");
		String rawName = oauth2User.getAttribute("name");

		if (!StringUtils.hasText(email)) {
			throw new OAuth2AuthenticationException("email attribute is required");
		}
		final String displayName = StringUtils.hasText(rawName) ? rawName : email;

		Member member = memberRepository.findByEmail(email)
			.map(existing -> {
				existing.setName(displayName);
				if (StringUtils.hasText(providerId)) {
					existing.setProviderId(providerId);
				}
				existing.setProvider(registrationId);
				return memberRepository.save(existing);
			})
			.orElseGet(() -> memberRepository.save(Member.builder()
				.email(email)
				.name(displayName)
				.password(null)
				.role(Role.USER)
				.provider(registrationId)
				.providerId(providerId)
				.build()));

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())),
			oauth2User.getAttributes(),
			"sub"
		);
	}
}
