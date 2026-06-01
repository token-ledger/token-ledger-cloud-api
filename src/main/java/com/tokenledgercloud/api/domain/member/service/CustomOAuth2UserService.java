package com.tokenledgercloud.api.domain.member.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tokenledgercloud.api.domain.member.entity.Member;
import com.tokenledgercloud.api.domain.member.entity.Role;
import com.tokenledgercloud.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User;
		try {
			oauth2User = super.loadUser(userRequest);
		} catch (Exception e) {
			log.error("[OAuth2] 유저 정보 로드 실패: {}", e.getMessage(), e);
			throw e;
		}
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		String providerId;
		String email;
		String rawName;
		String nameAttributeKey;

		if ("kakao".equals(registrationId)) {
			Object idObj = oauth2User.getAttribute("id");
			providerId = idObj != null ? idObj.toString() : null;
			Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
			Map<String, Object> profile = kakaoAccount != null
				? (Map<String, Object>) kakaoAccount.get("profile") : null;
			rawName = profile != null ? (String) profile.get("nickname") : null;
			email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
			nameAttributeKey = "id";
		} else {
			// Google
			providerId = oauth2User.getAttribute("sub");
			email = oauth2User.getAttribute("email");
			rawName = oauth2User.getAttribute("name");
			nameAttributeKey = "sub";
		}

		final String displayName = StringUtils.hasText(rawName) ? rawName : providerId;
		log.info("[OAuth2] registrationId={}, providerId={}, displayName={}", registrationId, providerId, displayName);

		try {
			memberRepository.findByProviderAndProviderId(registrationId, providerId)
				.map(existing -> {
					existing.setName(displayName);
					existing.setEmail(email);
					return memberRepository.save(existing);
				})
				.orElseGet(() -> memberRepository.save(Member.builder()
					.email(email)
					.name(displayName)
					.passwordHash(null)
					.role(Role.USER)
					.provider(registrationId)
					.providerId(providerId)
					.build()));
		} catch (Exception e) {
			log.error("[OAuth2] 멤버 저장 실패: {}", e.getMessage(), e);
			throw e;
		}

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			oauth2User.getAttributes(),
			nameAttributeKey
		);
	}
}
