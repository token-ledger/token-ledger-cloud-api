package com.tokenledgercloud.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import com.tokenledgercloud.api.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/members").permitAll()
				.requestMatchers(HttpMethod.POST, "/internal/usage-logs").permitAll()
				.requestMatchers("/api/dashboard/**").permitAll()
				.requestMatchers("/dashboard").permitAll()
				.requestMatchers("/login", "/oauth2/**", "/error").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(form -> form.defaultSuccessUrl("/", true));
			//.oauth2Login(oauth2 -> oauth2
			//	.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
			//	.defaultSuccessUrl("/", true)	
			//);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
