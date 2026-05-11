package com.tokenledgercloud.api.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String name = authentication.getName(); // providerId (sub or id)
		String role = authentication.getAuthorities().stream()
			.findFirst()
			.map(auth -> auth.getAuthority().replace("ROLE_", ""))
			.orElse("USER");

		String token = jwtTokenProvider.createToken(name, role);

		// 토큰 전체를 로그에 남기면 그대로 인증 자격이 되므로 길이와 prefix 만 노출한다.
		String tokenPreview = token.length() > 10 ? token.substring(0, 10) : token;
		log.info("OAuth2 token issued - name={}, role={}, tokenLength={}, tokenPrefix={}...",
			name, role, token.length(), tokenPreview);

		String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
			.queryParam("token", token)
			.build().toUriString();

		log.info("OAuth2 redirect target = http://localhost:3000/oauth2/redirect?token=<{} chars, prefix={}...>",
			token.length(), tokenPreview);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
