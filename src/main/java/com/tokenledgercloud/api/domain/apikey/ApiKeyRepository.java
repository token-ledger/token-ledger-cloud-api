package com.tokenledgercloud.api.domain.apikey;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByApiKey(String apiKey);
    List<ApiKey> findByMemberId(Long memberId);
}
