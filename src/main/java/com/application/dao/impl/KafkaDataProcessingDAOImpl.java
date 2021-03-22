package com.application.dao.impl;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.application.constant.BankingAppConstant;
import com.application.dao.KafkaDataProcessingDAO;
import com.application.model.Balance;
import com.application.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Query;

@Repository
public class KafkaDataProcessingDAOImpl implements KafkaDataProcessingDAO {

	@Qualifier("MongoDatabase")
	@Autowired
	public MongoTemplate mongoTemplate;
	
	@Override
	public void saveBalance(Balance balance) {
		Document doc=null;
		try {
			doc = Document.parse(new ObjectMapper().writeValueAsString(balance));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mongoTemplate.save(balance,BankingAppConstant.BALANCE_COLLECTION_NAME);
		//mongoDatabase.getCollection("test").insertOne(doc);
		
	}

	@Override
	public void saveTransaction(Transaction transaction) {
		Document doc=null;
		try {
			doc = Document.parse(new ObjectMapper().writeValueAsString(transaction));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//mongoDatabase.getCollection("test").insertOne(doc);
		mongoTemplate.save(transaction,BankingAppConstant.TRANSACTION_COLLECTION_NAME);
		
	}

	@Override
	public Page<Transaction> getTransaction(String accountNbr, Date fromDate, Date toDate, String type,int page,int size) {
		List<Transaction> transactions = null;
		Query query=new Query();
		query.addCriteria(Criteria.where(BankingAppConstant.ACCNBR_KEY).is(accountNbr).and(BankingAppConstant.TRANSACTIONS_KEY).gte(fromDate).lte(toDate));
		if(null!=type&&!"".equals(type.trim())) {
		query.addCriteria(Criteria.where(BankingAppConstant.TYPE_KEY).is(type));
		}
		//mongoDatabase.getCollection("test").find
		Pageable pageable = PageRequest.of(page,size);
		transactions = mongoTemplate.find(query, Transaction.class, BankingAppConstant.TRANSACTION_COLLECTION_NAME);
		Page<Transaction> transactionsPage = PageableExecutionUtils.getPage(
				transactions,
		        pageable,
		        () -> mongoTemplate.count(query, Transaction.class));
		
		return transactionsPage;
		
	}

	@Override
	public Balance getBalance(String accountNbr) {
		Query query=new Query();
		query.addCriteria(Criteria.where(BankingAppConstant.ACCNBR_KEY).is(accountNbr));
		//mongoDatabase.getCollection("test").find
		return mongoTemplate.findOne(query, Balance.class, BankingAppConstant.BALANCE_COLLECTION_NAME);
		
	}

	

	

}
