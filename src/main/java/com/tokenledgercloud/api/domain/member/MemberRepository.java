package com.tokenledgercloud.api.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByProviderAndProviderId(String provider, String providerId);

	Optional<Member> findByProviderId(String providerId);
}
