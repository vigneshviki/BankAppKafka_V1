package com.application.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.exception.BankException;
import com.application.model.Balance;
import com.application.model.Constant;
import com.application.model.FilterKeys;
import com.application.model.SearchCriteria;
import com.application.model.Transaction;
import com.application.service.serviceImpl.KafkaDataProcessingServiceImpl;

@Validated
@RestController
public class AccountControllerImpl implements AccountController {
	
	@Autowired
	KafkaDataProcessingServiceImpl dataProcessingServiceImpl;
	
	

	private Map<String, Balance> balCache = new HashMap<>();

	private Map<String, ArrayList<Transaction>> txCache = new HashMap<>();

	@Override
	public ResponseEntity<Balance> getBalanceByAccountNumber(@PathVariable("accNum") String accNum)
			throws BankException {

		/*
		 * if (balCache.containsKey(accNum)) { return new
		 * ResponseEntity<>(balCache.get(accNum), HttpStatus.OK); }
		 */
		
		if (null!=accNum&&!"".equals(accNum)) {
			return new ResponseEntity<>(dataProcessingServiceImpl.getBalance(accNum), HttpStatus.OK);
		}

		throw new BankException("Account Number does not exists with the BANK");
	}

	@Override
	public ResponseEntity<Balance> createTransaction(@RequestBody Transaction transaction) throws BankException {

		if (transaction == null) {
			throw new BankException("Search key not found");
		}

		String accountNM = transaction.getAccountNumber();
		transaction.setTransactionTs(new Timestamp(System.currentTimeMillis()));

		Balance bal = new Balance();
		bal.setAccountNumber(accountNM);
		bal.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));

		if ("DEPOSITE".equals(transaction.getType())) {

			if (balCache.containsKey(accountNM)) {
				bal.setBalance(balCache.get(accountNM).getBalance() + transaction.getAmount());
			} else {
				bal.setBalance(transaction.getAmount());
			}

		} else {
			if (balCache.containsKey(accountNM)) {
				
				double finalBal = balCache.get(accountNM).getBalance() - transaction.getAmount();
				if(finalBal < 0 ) {
					throw new BankException("WITHDRAW is not applicable due to insufficient balance");
				}
				bal.setBalance(finalBal);
			} else {
				throw new BankException("WITHDRAW is not applicable due to insufficient balance");
			}
		}

		balCache.put(accountNM, bal);
		if (txCache.containsKey(accountNM)) {
			ArrayList<Transaction> list = txCache.get(accountNM);
			list.add(transaction);
			txCache.put(accountNM, list);

		} else {
			ArrayList<Transaction> list = new ArrayList<>();
			list.add(transaction);
			txCache.put(accountNM, list);
		}

		return new ResponseEntity<>(bal, HttpStatus.OK);

	}

	@Cacheable("balances")
	@Override
	public ResponseEntity<Map<String, Balance>> retrieveBalances() throws BankException {

		if (balCache == null) {
			throw new BankException("Search key not found");
		}
		

		return new ResponseEntity<>(balCache, HttpStatus.OK);
	}

	@Cacheable("transactions")
	@Override
	public ResponseEntity<Map<String, ArrayList<Transaction>>> retrieveTransactions() throws BankException {

		if (balCache == null) {
			throw new BankException("Search key not found");
		}

		return new ResponseEntity<>(txCache, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Transaction>> getInTimeRange(@RequestBody SearchCriteria searchCriteria)
			throws BankException {

		if (searchCriteria.getAccountNumber() == null || !txCache.containsKey(searchCriteria.getAccountNumber())) {
			throw new BankException("Account Number not present with us");
		}

		List<Transaction> result = txCache.get(searchCriteria.getAccountNumber());

		if (FilterKeys.TODAY.equals(searchCriteria.timeRange)) {

			result = result.stream().filter(o -> {
				return o.getTransactionTs().toLocalDateTime().toLocalDate().isEqual(LocalDate.now());
			}).collect(Collectors.toList());

		} else if (FilterKeys.IN_LAST_WEEK.equals(searchCriteria.timeRange)) {

			result = result.stream().filter(o -> {
				return o.getTransactionTs().toLocalDateTime().toLocalDate().isAfter(LocalDate.now().minusWeeks(Constant.ONE).minusDays(Constant.ONE));
			}).collect(Collectors.toList());

		} else if (FilterKeys.LAST_MONTH.equals(searchCriteria.timeRange)) {

			result = result.stream().filter(o -> {
				return o.getTransactionTs().toLocalDateTime().toLocalDate().isAfter(LocalDate.now().minusMonths(Constant.ONE).minusDays(Constant.ONE));
			}).collect(Collectors.toList());
			
		} else if (FilterKeys.DATE_RANGE.equals(searchCriteria.timeRange)) {

			result = result.stream().filter(o -> {
				return (o.getTransactionTs().toLocalDateTime().toLocalDate().isAfter(searchCriteria.fromDate.minusDays(Constant.ONE))
						&& o.getTransactionTs().toLocalDateTime().toLocalDate().isBefore(searchCriteria.toDate.plusDays(Constant.ONE)));
			}).collect(Collectors.toList());

		}

		/*
		 * further filter on DEPOSITE OR WITHDRAW
		 */

		if (searchCriteria.type != null) {
			result = result.stream().filter(o -> {
				return o.getType().equals(searchCriteria.type);
			}).collect(Collectors.toList());
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> healthCheck(String name) {
		
		/*
		 * String
		 * msg="{'accountNumber': 'abcd', 'lastUpdateTimestamp': '2020-01-01T01:02:03.8Z', 'balance': 89.1}"
		 * ; //genericMessage.getPayload().toString(); System.out.println(msg);
		 * JSONObject balanceJson=new JSONObject(msg); Balance balance=new Balance();
		 * balance.setAccountNumber(balanceJson.getString("accountNumber"));
		 * balance.setBalance(balanceJson.getDouble("balance")); DateTimeFormatter
		 * formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'",
		 * Locale.ENGLISH); LocalDateTime date =
		 * LocalDateTime.parse(balanceJson.getString("lastUpdateTimestamp"), formatter);
		 * balance.setLastUpdateTimestamp(Timestamp.valueOf(date));
		 */
		/*
		 * ConnectionString connString = new ConnectionString(
		 * "mongodb+srv://root:root@cluster0.em1no.mongodb.net/test?retryWrites=true&w=majority"
		 * ); MongoClientSettings settings = MongoClientSettings.builder()
		 * .applyConnectionString(connString) .retryWrites(true) .build(); MongoClient
		 * mongoClient = MongoClients.create(settings); MongoDatabase database =
		 * mongoClient.getDatabase("test");
		 */
			
		
		//balanceRepository.insert(balance);

		return new ResponseEntity<>("Hi " + name + ", Service is up!", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Page<Transaction>> getInTimeRange(String accNum, String fromDate, String toDate, String type,int page,int size)
			throws BankException {
		
		return new ResponseEntity<>(dataProcessingServiceImpl.getTransaction(accNum, fromDate, toDate, type,page,size), HttpStatus.OK);
		
	}
	
	
}
