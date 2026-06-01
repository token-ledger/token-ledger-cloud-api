package com.tokenledgercloud.api.domain.project;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tokenledgercloud.api.domain.member.Member;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "projects",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_projects_external_id", columnNames = "external_id"),
		@UniqueConstraint(name = "uk_projects_member_project_key", columnNames = {"member_id", "project_key"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_id", nullable = false, unique = true, length = 50)
	private String externalId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false)
	private String name;

	@Column(name = "project_key", nullable = false)
	private String projectKey;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "project_environments", joinColumns = @JoinColumn(name = "project_id"))
	@Column(name = "environment", nullable = false, length = 50)
	@Builder.Default
	private List<String> environments = new ArrayList<>();

	@Column(name = "default_model", nullable = false)
	private String defaultModel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	@Builder.Default
	private ProjectStatus status = ProjectStatus.ACTIVE;

	@Column(name = "monthly_budget_usd", precision = 18, scale = 2)
	private BigDecimal monthlyBudgetUsd;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
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
