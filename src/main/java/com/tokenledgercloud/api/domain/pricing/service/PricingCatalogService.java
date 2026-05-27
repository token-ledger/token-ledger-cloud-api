package com.tokenledgercloud.api.domain.pricing.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokenledgercloud.api.domain.pricing.entity.PricingCatalog;
import com.tokenledgercloud.api.domain.pricing.repository.PricingCatalogRepository;
import com.tokenledgercloud.api.global.exception.PricingCatalogNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingCatalogService {

	private static final String DEFAULT_CATALOG_KEY = "default";

	private final PricingCatalogRepository pricingCatalogRepository;

	@Transactional(readOnly = true)
	public PricingCatalog getDefaultCatalog(String version) {
		String normalizedVersion = blankToNull(version);

		if (normalizedVersion != null) {
			return pricingCatalogRepository.findByCatalogKeyAndVersion(
					DEFAULT_CATALOG_KEY,
					normalizedVersion
				)
				.orElseThrow(PricingCatalogNotFoundException::new);
		}

		return pricingCatalogRepository.findLatestActiveCatalog(
				DEFAULT_CATALOG_KEY,
				PageRequest.of(0, 1)
			)
			.stream()
			.findFirst()
			.orElseThrow(PricingCatalogNotFoundException::new);
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}
}