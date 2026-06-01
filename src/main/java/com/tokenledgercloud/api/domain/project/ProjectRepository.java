package com.tokenledgercloud.api.domain.project;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	boolean existsByExternalId(String externalId);

	boolean existsByMemberIdAndProjectKey(Long memberId, String projectKey);

	Optional<Project> findByExternalIdAndMemberId(String externalId, Long memberId);

	List<Project> findByIdInAndMemberId(Collection<Long> ids, Long memberId);

	@Query("""
		select distinct p
		from Project p
		left join p.environments e
		where p.member.id = :memberId
		  and (:environment is null or e = :environment)
		  and (:status is null or p.status = :status)
		order by p.createdAt desc
	""")
	List<Project> findProjects(Long memberId, String environment, ProjectStatus status);
}
