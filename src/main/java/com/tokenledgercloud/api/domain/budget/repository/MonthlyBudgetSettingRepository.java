package com.tokenledgercloud.api.domain.budget.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tokenledgercloud.api.domain.budget.entity.MonthlyBudgetSetting;

public interface MonthlyBudgetSettingRepository extends JpaRepository<MonthlyBudgetSetting, String> {

	Optional<MonthlyBudgetSetting> findFirstByOrganizationIdAndProjectIdAndEnvironment(
		String organizationId,
		String projectId,
		String environment
	);

	Optional<MonthlyBudgetSetting> findFirstByOrganizationIdAndProjectIdIsNullAndEnvironmentIsNull(
		String organizationId
	);
}