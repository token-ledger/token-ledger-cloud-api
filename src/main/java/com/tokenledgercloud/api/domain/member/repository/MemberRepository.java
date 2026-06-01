package com.tokenledgercloud.api.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tokenledgercloud.api.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByProviderAndProviderId(String provider, String providerId);

	Optional<Member> findByProviderId(String providerId);
}
