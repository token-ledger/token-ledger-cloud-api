package com.tokenledgercloud.api.domain.projectapikey.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectApiKey {

	@Id
	@Column(length = 36)
	private String id;

	@Column(name = "organization_id", nullable = false, length = 36)
	private String organizationId;

	@Column(name = "project_id", nullable = false, length = 36)
	private String projectId;

	@Column(length = 20)
	private String environment;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(name = "key_prefix", nullable = false, length = 30)
	private String keyPrefix;

	@Column(name = "key_hash", nullable = false)
	private String keyHash;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "last_used_at")
	private LocalDateTime lastUsedAt;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		if (id == null || id.isBlank()) {
			id = UUID.randomUUID().toString();
		}
		if (createdAt == null) {
			createdAt = now;
		}
		if (updatedAt == null) {
			updatedAt = now;
		}
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
