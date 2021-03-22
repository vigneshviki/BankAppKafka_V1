package com.application.dao;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.application.model.Balance;
import com.application.model.Transaction;

public interface KafkaDataProcessingDAO {

public void saveBalance(Balance balance);
	
public void saveTransaction(Transaction transaction);

public Page<Transaction> getTransaction(String accountNbr, Date fromDate,Date toDate,String Type,int page,int size);

public Balance getBalance(String accountNbr);
	
}
