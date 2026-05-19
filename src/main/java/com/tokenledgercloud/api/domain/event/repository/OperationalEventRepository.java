package com.tokenledgercloud.api.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tokenledgercloud.api.domain.event.entity.OperationalEvent;

public interface OperationalEventRepository extends JpaRepository<OperationalEvent, String> {

	@Query("""
		select e
		from OperationalEvent e
		where (:projectId is null or e.projectId = :projectId)
		  and (:environment is null or e.environment = :environment)
		  and (:type is null or e.eventType = :type)
		  and (:cursorOccurredAt is null or e.occurredAt < :cursorOccurredAt)
		order by e.occurredAt desc
	""")
	List<OperationalEvent> findEvents(
		String projectId,
		String environment,
		String type,
		LocalDateTime cursorOccurredAt,
		Pageable pageable
	);
}