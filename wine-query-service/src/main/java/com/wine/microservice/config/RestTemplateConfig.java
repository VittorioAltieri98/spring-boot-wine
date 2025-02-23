package com.wine.microservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Autowired
    private BearerTokenInterceptor bearerTokenInterceptor;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder
                .additionalInterceptors(bearerTokenInterceptor)
                .build();
    }

}
