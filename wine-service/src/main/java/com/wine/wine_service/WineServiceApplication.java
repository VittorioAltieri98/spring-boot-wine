package com.wine.wine_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class WineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WineServiceApplication.class, args);
	}

}
