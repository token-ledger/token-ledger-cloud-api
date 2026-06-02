package com.tokenledgercloud.api.global.exception;

public class PricingCatalogGenerationFailedException extends ApiException {

	public PricingCatalogGenerationFailedException() {
		super(ErrorCode.PRICING_CATALOG_GENERATION_FAILED);
	}
}