package com.tokenledgercloud.api.global.exception;

public class InvalidPeriodException extends ApiException {

	public InvalidPeriodException() {
		super(ErrorCode.INVALID_PERIOD);
	}
}
