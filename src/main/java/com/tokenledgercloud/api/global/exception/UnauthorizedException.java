package com.tokenledgercloud.api.global.exception;

public class UnauthorizedException extends ApiException {

	public UnauthorizedException() {
		super(ErrorCode.UNAUTHORIZED);
	}
}
