package com.tokenledgercloud.api.global.exception;

public class DuplicateMemberEmailException extends ApiException {

	public DuplicateMemberEmailException() {
		super(ErrorCode.DUPLICATE_MEMBER_EMAIL);
	}
}
