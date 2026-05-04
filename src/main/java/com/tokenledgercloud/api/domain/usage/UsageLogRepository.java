package com.tokenledgercloud.api.domain.usage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

	Optional<UsageLog> findByIdempotencyKey(String idempotencyKey);

	@Query("""
	select coalesce(sum(u.totalCost), 0) as totalCost,
	       coalesce(sum(u.totalTokens), 0) as totalTokens,
	       count(case when u.status = com.tokenledgercloud.api.domain.usage.UsageStatus.BLOCKED then 1 end) as blockedRequests
	from UsageLog u
	where (:projectId is null or u.projectId = :projectId)
	  and u.startedAt >= :from
	  and u.startedAt < :to
    """)
    KpiProjection getKpi(Long projectId, LocalDateTime from, LocalDateTime to);

	@Query("""
		select u.modelId as modelId,
		       coalesce(sum(u.totalCost), 0) as totalCost,
		       coalesce(sum(u.totalTokens), 0) as totalTokens
		from UsageLog u
		where (:projectId is null or u.projectId = :projectId)
		  and u.startedAt >= :from
		  and u.startedAt < :to
		group by u.modelId
		order by sum(u.totalCost) desc
	""")
	List<ModelCostSummaryProjection> findModelCostSummary(
		Long projectId,
		LocalDateTime from,
		LocalDateTime to
	);

	@Query("""
		select u.projectId as projectId,
		       coalesce(sum(u.totalCost), 0) as totalCost,
		       coalesce(sum(u.totalTokens), 0) as totalTokens
		from UsageLog u
		where u.startedAt >= :from
		  and u.startedAt < :to
		group by u.projectId
		order by sum(u.totalCost) desc
	""")
	List<ProjectCostRankingProjection> findProjectCostRanking(
		LocalDateTime from,
		LocalDateTime to
	);
}