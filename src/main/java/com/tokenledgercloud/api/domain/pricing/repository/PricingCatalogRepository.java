package com.tokenledgercloud.api.domain.pricing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tokenledgercloud.api.domain.pricing.entity.PricingCatalog;

public interface PricingCatalogRepository extends JpaRepository<PricingCatalog, String> {

	@Query("""
		select c
		from PricingCatalog c
		where c.catalogKey = :catalogKey
		  and c.isActive = true
		order by c.publishedAt desc
	""")
	List<PricingCatalog> findLatestActiveCatalog(
		String catalogKey,
		Pageable pageable
	);

	@Query("""
		select c
		from PricingCatalog c
		where c.catalogKey = :catalogKey
		  and c.version = :version
		  and c.isActive = true
	""")
	Optional<PricingCatalog> findByCatalogKeyAndVersion(
		String catalogKey,
		String version
	);
}