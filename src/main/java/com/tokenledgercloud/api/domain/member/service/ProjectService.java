package com.tokenledgercloud.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.member.Member;
import com.tokenledgercloud.api.domain.member.MemberRepository;
import com.tokenledgercloud.api.domain.project.Project;
import com.tokenledgercloud.api.domain.project.ProjectRepository;
import com.tokenledgercloud.api.domain.project.ProjectStatus;
import com.tokenledgercloud.api.domain.usage.KpiProjection;
import com.tokenledgercloud.api.domain.usage.ModelCostSummaryProjection;
import com.tokenledgercloud.api.domain.usage.ProjectUsageRankingProjection;
import com.tokenledgercloud.api.domain.usage.UsageLogRepository;
import com.tokenledgercloud.api.dto.project.ProjectCreateRequest;
import com.tokenledgercloud.api.dto.project.ProjectCreateResponse;
import com.tokenledgercloud.api.dto.project.ProjectDetailResponse;
import com.tokenledgercloud.api.dto.project.ProjectListItemResponse;
import com.tokenledgercloud.api.dto.project.ProjectListResponse;
import com.tokenledgercloud.api.dto.project.ProjectRankingItemResponse;
import com.tokenledgercloud.api.dto.project.ProjectRankingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private static final int MAX_RANKING_LIMIT = 100;

	private final ProjectRepository projectRepository;
	private final MemberRepository memberRepository;
	private final UsageLogRepository usageLogRepository;

	@Transactional
	public ProjectCreateResponse createProject(Authentication authentication, ProjectCreateRequest request) {
		Member member = getMember(authentication);
		if (projectRepository.existsByMemberIdAndProjectKey(member.getId(), request.projectKey())) {
			throw new IllegalArgumentException("projectKey already exists");
		}

		Project project = Project.builder()
			.externalId(generateExternalId())
			.member(member)
			.name(request.name())
			.projectKey(request.projectKey())
			.environments(request.environments())
			.defaultModel(request.defaultModel())
			.status(ProjectStatus.ACTIVE)
			.build();

		return toCreateResponse(projectRepository.save(project));
	}

	@Transactional(readOnly = true)
	public ProjectListResponse getProjects(Authentication authentication, String environment, String status) {
		Member member = getMember(authentication);
		ProjectStatus projectStatus = parseStatus(status);

		List<ProjectListItemResponse> items = projectRepository.findProjects(member.getId(), blankToNull(environment), projectStatus)
			.stream()
			.map(this::toListItemResponse)
			.toList();

		return new ProjectListResponse(items);
	}

	@Transactional(readOnly = true)
	public ProjectDetailResponse getProject(Authentication authentication, String projectId) {
		Member member = getMember(authentication);
		Project project = getProjectForMember(projectId, member);
		TimeRange month = currentMonthRange();
		KpiProjection kpi = usageLogRepository.getKpi(project.getId(), month.from(), month.to());

		return new ProjectDetailResponse(
			project.getExternalId(),
			project.getName(),
			project.getProjectKey(),
			project.getStatus(),
			List.copyOf(project.getEnvironments()),
			project.getDefaultModel(),
			kpi.getTotalCost(),
			kpi.getTotalTokens()
		);
	}

	@Transactional(readOnly = true)
	public ProjectRankingResponse getProjectRanking(
		Authentication authentication,
		String environment,
		String period,
		Integer limit
	) {
		validateLimit(limit);
		Member member = getMember(authentication);
		TimeRange range = resolvePeriod(period);
		List<Project> projects = projectRepository.findProjects(member.getId(), blankToNull(environment), ProjectStatus.ACTIVE);
		if (projects.isEmpty()) {
			return new ProjectRankingResponse(List.of());
		}

		Map<Long, Project> projectById = projects.stream()
			.collect(Collectors.toMap(Project::getId, Function.identity()));
		List<Long> projectIds = projects.stream()
			.map(Project::getId)
			.toList();

		List<ProjectUsageRankingProjection> rankings = usageLogRepository.findProjectUsageRanking(
				projectIds,
				range.from(),
				range.to()
			)
			.stream()
			.filter(row -> projectById.containsKey(row.getProjectId()))
			.sorted(Comparator.comparing(ProjectUsageRankingProjection::getTotalCost).reversed()
				.thenComparing(ProjectUsageRankingProjection::getLatestUsedAt, Comparator.nullsLast(Comparator.reverseOrder())))
			.limit(limit)
			.toList();

		List<ProjectRankingItemResponse> items = new ArrayList<>();
		for (int i = 0; i < rankings.size(); i++) {
			ProjectUsageRankingProjection row = rankings.get(i);
			items.add(toRankingItem(row, projectById.get(row.getProjectId()), environment, range, i + 1));
		}

		return new ProjectRankingResponse(items);
	}

	private ProjectRankingItemResponse toRankingItem(
		ProjectUsageRankingProjection row,
		Project project,
		String requestedEnvironment,
		TimeRange range,
		int rank
	) {
		String topModel = usageLogRepository.findTopModelsByProjectId(project.getId(), range.from(), range.to())
			.stream()
			.findFirst()
			.map(ModelCostSummaryProjection::getModelId)
			.orElse(project.getDefaultModel());

		return new ProjectRankingItemResponse(
			project.getExternalId(),
			project.getName(),
			resolveResponseEnvironment(project, requestedEnvironment),
			topModel,
			row.getTotalCost(),
			calculateBudgetUsagePercent(row.getTotalCost(), project.getMonthlyBudgetUsd()),
			rank
		);
	}

	private void validateLimit(Integer limit) {
		if (limit == null || limit < 1 || limit > MAX_RANKING_LIMIT) {
			throw new IllegalArgumentException("INVALID_LIMIT");
		}
	}

	private int calculateBudgetUsagePercent(BigDecimal costUsd, BigDecimal monthlyBudgetUsd) {
		if (monthlyBudgetUsd == null || monthlyBudgetUsd.compareTo(BigDecimal.ZERO) <= 0) {
			return 0;
		}
		return costUsd
			.multiply(BigDecimal.valueOf(100))
			.divide(monthlyBudgetUsd, 0, RoundingMode.HALF_UP)
			.intValue();
	}

	private Project getProjectForMember(String projectId, Member member) {
		return projectRepository.findByExternalIdAndMemberId(projectId, member.getId())
			.orElseThrow(() -> new IllegalArgumentException("Project not found"));
	}

	private ProjectCreateResponse toCreateResponse(Project project) {
		return new ProjectCreateResponse(
			project.getExternalId(),
			project.getName(),
			project.getProjectKey(),
			project.getStatus()
		);
	}

	private ProjectListItemResponse toListItemResponse(Project project) {
		return new ProjectListItemResponse(
			project.getExternalId(),
			project.getName(),
			List.copyOf(project.getEnvironments()),
			project.getDefaultModel(),
			project.getStatus()
		);
	}

	private String resolveResponseEnvironment(Project project, String requestedEnvironment) {
		if (requestedEnvironment != null && !requestedEnvironment.isBlank()) {
			return requestedEnvironment;
		}
		return project.getEnvironments().isEmpty() ? null : project.getEnvironments().get(0);
	}

	private ProjectStatus parseStatus(String status) {
		if (status == null || status.isBlank()) {
			return null;
		}
		return ProjectStatus.valueOf(status);
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private String generateExternalId() {
		String externalId;
		do {
			externalId = "proj_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		}
		while (projectRepository.existsByExternalId(externalId));
		return externalId;
	}

	private Member getMember(Authentication authentication) {
		String identifier = authentication.getName();
		return memberRepository.findByEmail(identifier)
			.or(() -> memberRepository.findByProviderId(identifier))
			.orElseThrow(() -> new IllegalStateException("Member not found"));
	}

	private TimeRange currentMonthRange() {
		LocalDate today = LocalDate.now();
		return new TimeRange(today.withDayOfMonth(1).atStartOfDay(), today.plusDays(1).atStartOfDay());
	}

	private TimeRange resolvePeriod(String period) {
		LocalDate today = LocalDate.now();

		return switch (period == null ? "month" : period) {
			case "today" -> new TimeRange(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
			case "week" -> new TimeRange(today.minusDays(6).atStartOfDay(), today.plusDays(1).atStartOfDay());
			case "month" -> currentMonthRange();
			default -> new TimeRange(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
		};
	}

	private record TimeRange(LocalDateTime from, LocalDateTime to) {
	}
}
