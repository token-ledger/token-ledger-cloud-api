package com.tokenledgercloud.api.domain.pricing.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingPlan {

	@Id
	@Column(length = 36)
	private String id;

	@Column(name = "catalog_id", length = 36)
	private String catalogId;

	@Column(nullable = false, length = 50)
	private String provider;

	@Column(nullable = false, length = 100)
	private String model;

	@Column(nullable = false, length = 10)
	private String currency;

	@Column(name = "prompt_rate", nullable = false, precision = 18, scale = 6)
	private BigDecimal promptRate;

	@Column(name = "completion_rate", nullable = false, precision = 18, scale = 6)
	private BigDecimal completionRate;

	@Column(name = "reasoning_rate", precision = 18, scale = 6)
	private BigDecimal reasoningRate;

	@Column(name = "cached_prompt_rate", precision = 18, scale = 6)
	private BigDecimal cachedPromptRate;

	@Column(nullable = false, length = 50)
	private String version;

	@Column(name = "effective_from", nullable = false)
	private LocalDateTime effectiveFrom;

	@Column(name = "effective_to")
	private LocalDateTime effectiveTo;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	void prePersist() {
		if (id == null || id.isBlank()) {
			id = UUID.randomUUID().toString();
		}
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (reasoningRate == null) {
			reasoningRate = BigDecimal.ZERO;
		}
		if (cachedPromptRate == null) {
			cachedPromptRate = BigDecimal.ZERO;
		}
	}
}