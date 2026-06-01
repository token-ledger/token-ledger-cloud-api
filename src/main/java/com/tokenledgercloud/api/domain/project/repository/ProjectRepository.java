package com.tokenledgercloud.api.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tokenledgercloud.api.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, String> {

	Optional<Project> findByOrganizationIdAndProjectKey(String organizationId, String projectKey);
}
