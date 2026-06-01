package com.tokenledgercloud.api.global.exception;

public class BudgetNotFoundException extends ApiException {
	public BudgetNotFoundException() {
		super(ErrorCode.BUDGET_NOT_FOUND);
	}
}