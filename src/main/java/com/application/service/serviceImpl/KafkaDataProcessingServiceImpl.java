package com.application.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.application.constant.BankingAppConstant;
import com.application.dao.impl.KafkaDataProcessingDAOImpl;
import com.application.model.Balance;
import com.application.model.Transaction;
import com.application.service.kafkaDataProcessingService;

@Service
public class KafkaDataProcessingServiceImpl implements kafkaDataProcessingService {

	@Autowired
	KafkaDataProcessingDAOImpl kafkaDataProcessingDAOImpl;
	
	@Override
	public void saveBalance(Balance balance) {
		kafkaDataProcessingDAOImpl.saveBalance(balance);
		
	}

	@Override
	public void saveTransaction(Transaction transaction) {
		kafkaDataProcessingDAOImpl.saveTransaction(transaction);
		
	}

	@Override
	public Page<Transaction> getTransaction(String accountNbr, String fromDateString, String toDateString, String type,int page,int size) {
		try {
		SimpleDateFormat format=new SimpleDateFormat(BankingAppConstant.DATE_FORMAT);
		Date fromDate=format.parse(fromDateString);
		Date toDate= format.parse(toDateString);
		
		return kafkaDataProcessingDAOImpl.getTransaction(accountNbr, fromDate, toDate, type,page,size);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Balance getBalance(String accountNbr) {
		try {
			
			return kafkaDataProcessingDAOImpl.getBalance(accountNbr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

}
