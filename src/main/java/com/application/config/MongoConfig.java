package com.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.application.constant.BankingAppConstant;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class MongoConfig {
	

	@Bean("MongoDatabase")
	public MongoTemplate mongoTemplate() {
		ConnectionString connString = new ConnectionString(
				  "mongodb+srv://root:root@cluster0.em1no.mongodb.net/test?retryWrites=true&w=majority"
				  );
		MongoClientSettings settings = MongoClientSettings.builder()
			    .applyConnectionString(connString)
			    .retryWrites(true)
			    .build();
			MongoClient mongoClient = MongoClients.create(settings);
			
			//MongoDatabase database = mongoClient.getDatabase("test");
		
		return new MongoTemplate(mongoClient,BankingAppConstant.DATABASE);
	}
	


}
