package com.tokenledgercloud.api.domain.pricing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tokenledgercloud.api.domain.pricing.entity.PricingPlan;

public interface PricingPlanRepository extends JpaRepository<PricingPlan, String> {

	@Query("""
		select p
		from PricingPlan p
		where (:provider is null or p.provider = :provider)
		  and (:model is null or p.model = :model)
		  and (:activeOnly = false or p.effectiveTo is null)
		order by p.provider asc, p.model asc, p.effectiveFrom desc
	""")
	List<PricingPlan> findPricingPlans(
		String provider,
		String model,
		boolean activeOnly
	);
}