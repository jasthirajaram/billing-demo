package com.billingusers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class CloudBillingUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudBillingUsersApplication.class, args);
	}
	
//	@Bean
//	public ModelMapper modelMapper() {
//		return new ModelMapper();
//	}
}