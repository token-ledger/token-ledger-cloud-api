package com.tokenledgercloud.api.global.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApiResponse<T>(
	boolean success,
	String code,
	String message,
	T data,
	List<ApiFieldError> errors,
	LocalDateTime timestamp
) {

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "SUCCESS", "Request processed successfully.", data, List.of(), LocalDateTime.now());
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, "SUCCESS", message, data, List.of(), LocalDateTime.now());
	}

	public static ApiResponse<Void> error(String code, String message) {
		return new ApiResponse<>(false, code, message, null, List.of(), LocalDateTime.now());
	}

	public static ApiResponse<Void> error(String code, String message, List<ApiFieldError> errors) {
		return new ApiResponse<>(false, code, message, null, errors, LocalDateTime.now());
	}
}
