package com.tokenledgercloud.api.global.response;

public record ApiFieldError(
	String field,
	Object rejectedValue,
	String reason
) {
}
