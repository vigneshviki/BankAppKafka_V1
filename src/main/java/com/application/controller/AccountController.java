package com.application.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.application.exception.BankException;
import com.application.model.Balance;
import com.application.model.SearchCriteria;
import com.application.model.Transaction;

public interface AccountController {

	/**
	 * 
	 * @param accNum accountNbr has to be provide
	 * @return single balance object
	 * @throws BankException
	 */
	@RequestMapping("/v1/getBalanceByAccount/{accNum}")
	ResponseEntity<Balance> getBalanceByAccountNumber(@PathVariable("accNum") String accNum) throws BankException;

	@PostMapping("/v1/createTransaction")
	ResponseEntity<Balance> createTransaction(@RequestBody Transaction balance) throws BankException;

	@RequestMapping("/v1/healthCheck/{name}")
	ResponseEntity<String> healthCheck(@PathVariable("name") String name) throws BankException;

	@RequestMapping("/v1/retrieveBalances")
	ResponseEntity<Map<String, Balance>> retrieveBalances() throws BankException;
	
	@RequestMapping("/v1/retrieveTransactions")
	ResponseEntity<Map<String, ArrayList<Transaction>>> retrieveTransactions() throws BankException;
	
	@PostMapping("/v1/getInTimeRange")
	ResponseEntity<List<Transaction>> getInTimeRange(@RequestBody SearchCriteria searchCriteri) throws BankException;
	
	@PostMapping("/v1/getInTimeRanges")
	ResponseEntity<Page<Transaction>> getInTimeRange(@PathVariable("accNum") String accNum,@PathVariable("fromDate") String fromDate,@PathVariable("toDate") String toDate,@PathVariable("type") String type,
			@PathVariable("page") int page,
			@PathVariable("size") int size) throws BankException;
}
