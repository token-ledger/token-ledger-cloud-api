package com.tokenledgercloud.api.domain.usage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
	name = "usage_logs",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_usage_logs_idempotency_key", columnNames = "idempotency_key")
	},
	indexes = {
		@Index(name = "idx_usage_project_started", columnList = "project_id, started_at"),
		@Index(name = "idx_usage_model_started", columnList = "model_id, started_at")
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false)
	private String eventId;

	@Column(name = "idempotency_key", nullable = false, unique = true)
	private String idempotencyKey;

	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "model_id", nullable = false)
	private String modelId;

	@Column(name = "input_tokens", nullable = false)
	private Long inputTokens;

	@Column(name = "output_tokens", nullable = false)
	private Long outputTokens;

	@Column(name = "total_tokens", nullable = false)
	private Long totalTokens;

	@Column(name = "total_cost", nullable = false, precision = 18, scale = 8)
	private BigDecimal totalCost;

	@Column(name = "currency_code", nullable = false, length = 10)
	private String currencyCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private UsageStatus status;

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "finished_at")
	private LocalDateTime finishedAt;

	@Column(name = "latency_ms")
	private Long latencyMs;

	@Column(name = "error_code")
	private String errorCode;

	@Column(name = "error_message", length = 1000)
	private String errorMessage;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (currencyCode == null || currencyCode.isBlank()) {
			currencyCode = "USD";
		}
		if (totalTokens == null) {
			totalTokens = safe(inputTokens) + safe(outputTokens);
		}
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}