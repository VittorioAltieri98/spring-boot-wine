package com.wine.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.wine.microservice"})
public class WineCommandApplication {

	public static void main(String[] args) {
		SpringApplication.run(WineCommandApplication.class, args);
	}

}
