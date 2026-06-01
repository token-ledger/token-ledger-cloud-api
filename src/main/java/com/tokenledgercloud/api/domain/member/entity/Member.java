package com.tokenledgercloud.api.domain.member.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	@Id
	@Column(length = 36)
	private String id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(name = "password_hash")
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Role role;

	@Column(nullable = false, length = 30)
	private String provider;

	@Column(name = "provider_id", length = 100)
	private String providerId;

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
