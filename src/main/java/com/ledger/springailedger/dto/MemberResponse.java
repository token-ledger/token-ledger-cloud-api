package com.ledger.springailedger.dto;

import com.ledger.springailedger.domain.member.Member;

public record MemberResponse(Long id, String email, String name, String role, String provider) {

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
