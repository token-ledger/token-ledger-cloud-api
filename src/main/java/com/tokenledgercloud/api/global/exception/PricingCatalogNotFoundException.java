package com.tokenledgercloud.api.global.exception;

public class PricingCatalogNotFoundException extends ApiException {

	public PricingCatalogNotFoundException() {
		super(ErrorCode.PRICING_CATALOG_NOT_FOUND);
	}
}