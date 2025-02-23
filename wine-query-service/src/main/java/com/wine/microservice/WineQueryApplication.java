package com.wine.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WineQueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(WineQueryApplication.class, args);
	}

}
