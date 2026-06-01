package com.tokenledgercloud.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
	boolean success,
	T data,
	String message,
	String errorCode
) {

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, data, message, null);
	}

	public static <T> ApiResponse<T> error(String errorCode, String message) {
		return new ApiResponse<>(false, null, message, errorCode);
	}
}
