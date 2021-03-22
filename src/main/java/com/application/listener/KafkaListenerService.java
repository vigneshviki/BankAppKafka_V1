package com.application.listener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.application.constant.BankingAppConstant;
import com.application.model.Balance;
import com.application.model.Transaction;
import com.application.service.serviceImpl.KafkaDataProcessingServiceImpl;

@Component
public class KafkaListenerService {

	@Autowired
	KafkaDataProcessingServiceImpl dataProcessingServiceImpl;
	
	@KafkaListener(topics = "testBalanceTopic",groupId = "balanceConsumer")
	public void consumeBalance(GenericMessage genericMessage) {
		//{"accountNumber": "abc", "lastUpdateTimestamp": "2020-01-01T01:02:03.8Z", "balance": 89.1}
		String msg=genericMessage.getPayload().toString();
		System.out.println(msg);
		JSONObject balanceJson=new JSONObject(msg);
		Balance balance=new Balance();
		balance.setAccountNumber(balanceJson.getString(BankingAppConstant.ACCNBR_KEY));
		balance.setBalance(balanceJson.getDouble("balance"));
		DateTimeFormatter formatter = 
		        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
		LocalDateTime date = LocalDateTime.parse(balanceJson.getString("lastUpdateTimestamp"), formatter);
		balance.setLastUpdateTimestamp(Timestamp.valueOf(date));
		dataProcessingServiceImpl.saveBalance(balance);
	}
	
	@KafkaListener(topics = "testTransactionTopic",groupId = "transactionConsumer")
	public void consumeTransaction(GenericMessage genericMessage) {
		//{"accountNumber": "abc", "transactionTs": "2020-01-03T01:02:03.8Z", "type": "DEPOSIT", "amount": 89.1}
		String msg=genericMessage.getPayload().toString();
		System.out.println(msg);
		JSONObject transactionJson=new JSONObject(msg);
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(transactionJson.getString(""));
		transaction.setAmount(transactionJson.getDouble("amount"));
		DateTimeFormatter formatter = 
		        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
		LocalDateTime date = LocalDateTime.parse(transactionJson.getString(BankingAppConstant.TRANSACTIONS_KEY), formatter);
		transaction.setTransactionTs(Timestamp.valueOf(date));
		transaction.setType(transactionJson.getString(BankingAppConstant.TYPE_KEY));
		dataProcessingServiceImpl.saveTransaction(transaction);
	}
	
	
}
