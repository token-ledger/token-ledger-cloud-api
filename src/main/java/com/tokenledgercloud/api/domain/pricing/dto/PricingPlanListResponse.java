package com.tokenledgercloud.api.domain.pricing.dto;

import java.util.List;

public record PricingPlanListResponse(
	List<PricingPlanItemResponse> items
) {
}