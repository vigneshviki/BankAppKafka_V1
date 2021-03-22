package com.application.service;

import org.springframework.data.domain.Page;

import com.application.model.Balance;
import com.application.model.Transaction;

public interface kafkaDataProcessingService {

	public void saveBalance(Balance balance);
	
	public void saveTransaction(Transaction transaction);
	
	public Page<Transaction> getTransaction(String accountNbr, String fromDate,String toDate,String type,int page,int size);

	public Balance getBalance(String accountNbr);
}
