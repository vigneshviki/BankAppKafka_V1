package com.application.model;

import java.time.LocalDate;

public class SearchCriteria {

	public LocalDate toDate;

	public LocalDate fromDate;

	private String accountNumber;

	public FilterKeys timeRange;

	/*
	 * Type can be DEPOSITE OR WITHDRAW
	 */

	public String type;

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
