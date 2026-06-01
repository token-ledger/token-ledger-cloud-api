package com.tokenledgercloud.api.global.exception;

public class InvalidMonthException extends ApiException {
	public InvalidMonthException() {
		super(ErrorCode.INVALID_MONTH);
	}
}