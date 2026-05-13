package com.tokenledgercloud.api.global.exception;

public class UnsupportedAuthenticationException extends ApiException {

	public UnsupportedAuthenticationException() {
		super(ErrorCode.UNSUPPORTED_AUTHENTICATION);
	}
}
