package com.tokenledgercloud.api.domain.event.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operational_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationalEvent {

	@Id
	@Column(length = 36)
	private String id;

	@Column(name = "organization_id", nullable = false, length = 36)
	private String organizationId;

	@Column(name = "project_id", length = 36)
	private String projectId;

	@Column(length = 20)
	private String environment;

	@Column(name = "event_type", nullable = false, length = 20)
	private String eventType;

	@Column(name = "event_code", nullable = false, length = 50)
	private String eventCode;

	@Column(nullable = false, columnDefinition = "text")
	private String message;

	@Column(name = "metadata_json", columnDefinition = "json")
	private String metadataJson;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void prePersist() {
		if (id == null || id.isBlank()) {
			id = UUID.randomUUID().toString();
		}
		if (occurredAt == null) {
			occurredAt = LocalDateTime.now();
		}
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}
}