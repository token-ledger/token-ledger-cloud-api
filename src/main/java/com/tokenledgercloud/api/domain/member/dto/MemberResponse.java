package com.tokenledgercloud.api.domain.member.dto;

import com.tokenledgercloud.api.domain.member.entity.Member;

public record MemberResponse(String id, String email, String name, String role, String provider) {

	public static MemberResponse from(Member member) {
		return new MemberResponse(
			member.getId(),
			member.getEmail(),
			member.getName(),
			member.getRole().name(),
			member.getProvider()
		);
	}
}
