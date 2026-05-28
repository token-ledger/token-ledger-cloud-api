package com.tokenledgercloud.api.global.exception;

public class InvalidPricingEffectivePeriodException extends ApiException {

	public InvalidPricingEffectivePeriodException() {
		super(ErrorCode.INVALID_PRICING_EFFECTIVE_PERIOD);
	}
}