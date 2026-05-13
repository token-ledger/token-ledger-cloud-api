package com.tokenledgercloud.api.global.exception;

public class MemberNotFoundException extends ApiException {

	public MemberNotFoundException() {
		super(ErrorCode.MEMBER_NOT_FOUND);
	}
}
