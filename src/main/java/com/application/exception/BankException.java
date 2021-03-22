package com.application.exception;
public class BankException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public BankException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public BankException() {
		super();
	}
}