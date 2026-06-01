package com.tokenledgercloud.api.domain.projectapikey.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tokenledgercloud.api.domain.projectapikey.entity.ProjectApiKey;

public interface ProjectApiKeyRepository extends JpaRepository<ProjectApiKey, String> {

	List<ProjectApiKey> findByKeyPrefixInAndStatus(Collection<String> keyPrefixes, String status);
}
