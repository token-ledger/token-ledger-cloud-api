package com.tokenledgercloud.api.domain.apikey;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {
    Optional<ApiKey> findByHashedKeyAndIsActiveTrue(String hashedKey);
    List<ApiKey> findByMemberId(String memberId);
    long countByMemberId(String memberId);
}
