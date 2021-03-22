package com.application.model;

import java.sql.Timestamp;


public class Transaction {
	
	private String accountNumber;
	
	private Timestamp transactionTs;
	
	private String type;
	
	private double amount; 
	
	public String getAccountNumber(){
		return this.accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public Timestamp getTransactionTs() {
		return this.transactionTs;
	}
	
	public void setTransactionTs(Timestamp transactionTs) {
		this.transactionTs = transactionTs ;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public double getAmount() {
		return this.amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount ;
	}
}
