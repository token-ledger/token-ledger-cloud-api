package com.tokenledgercloud.api.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON-400", "Invalid request input."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "Authentication is required."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-403", "You do not have permission to access this resource."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "The requested resource was not found."),
	CONFLICT(HttpStatus.CONFLICT, "COMMON-409", "The request conflicts with current resource state."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "An unexpected server error occurred."),
	DUPLICATE_MEMBER_EMAIL(HttpStatus.CONFLICT, "MEMBER-409", "Email already registered."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-404", "Member not found."),
	UNSUPPORTED_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "AUTH-401", "Unsupported authentication type."),
	INVALID_MONTH(HttpStatus.BAD_REQUEST, "BUDGET-400", "Invalid month format."),
	INVALID_PERIOD_TYPE(HttpStatus.BAD_REQUEST, "BUDGET-401", "Unsupported budget period type."),
	BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "BUDGET-404", "Budget not found."),
	INVALID_EVENT_TYPE(HttpStatus.BAD_REQUEST, "EVENT-400", "Invalid event type."),
	INVALID_PERIOD(HttpStatus.BAD_REQUEST, "DASHBOARD-400", "Unsupported period. Use today, week, or month.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
