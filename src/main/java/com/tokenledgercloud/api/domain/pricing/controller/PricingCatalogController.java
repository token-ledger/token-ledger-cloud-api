package com.tokenledgercloud.api.domain.pricing.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tokenledgercloud.api.domain.pricing.entity.PricingCatalog;
import com.tokenledgercloud.api.domain.pricing.service.PricingCatalogService;
import com.tokenledgercloud.api.global.exception.PricingCatalogGenerationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pricing-catalogs")
public class PricingCatalogController {

	private static final String YAML_MEDIA_TYPE = "application/x-yaml";

	private final PricingCatalogService pricingCatalogService;

	@GetMapping(
		value = "/default.yml",
		produces = YAML_MEDIA_TYPE
	)
	public ResponseEntity<String> getDefaultYaml(
		@RequestParam(required = false) String version,
		@RequestParam(required = false) String checksum,
		@RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
	) {
		PricingCatalog catalog = pricingCatalogService.getDefaultCatalog(version);

		if (catalog.getGeneratedYaml() == null || catalog.getGeneratedYaml().isBlank()) {
			throw new PricingCatalogGenerationFailedException();
		}

		String etag = toEtag(catalog.getChecksum());

		if (etag.equals(ifNoneMatch) || catalog.getChecksum().equals(checksum) || etag.equals(checksum)) {
			return ResponseEntity.status(304)
				.eTag(etag)
				.cacheControl(CacheControl.noCache())
				.build();
		}

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(YAML_MEDIA_TYPE))
			.header(HttpHeaders.ETAG, etag)
			.header(HttpHeaders.LAST_MODIFIED, toHttpDate(catalog))
			.cacheControl(CacheControl.noCache())
			.body(catalog.getGeneratedYaml());
	}

	private String toEtag(String checksum) {
		return "\"" + checksum + "\"";
	}

	private String toHttpDate(PricingCatalog catalog) {
		LocalDateTime lastModified = catalog.getPublishedAt();

		if (lastModified == null) {
			lastModified = catalog.getUpdatedAt();
		}

		if (lastModified == null) {
			lastModified = catalog.getCreatedAt();
		}

		if (lastModified == null) {
			lastModified = LocalDateTime.now(ZoneOffset.UTC);
		}

		return lastModified
			.atZone(ZoneOffset.UTC)
			.format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}
}